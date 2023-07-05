package com.eikona.mata.service.impl.model;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ScheduledFuture;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import com.eikona.mata.constants.ApplicationConstants;
import com.eikona.mata.constants.EmailScheduleConstants;
import com.eikona.mata.constants.NumberConstants;
import com.eikona.mata.dto.PaginationDto;
import com.eikona.mata.entity.EmailSchedule;
import com.eikona.mata.repository.EmailScheduleRepository;
import com.eikona.mata.service.EmailScheduleService;
import com.eikona.mata.util.GeneralSpecificationUtil;


@Service
@EnableScheduling
@Configurable
public class EmailScheduleServiceImpl implements EmailScheduleService{

	@Autowired
	private EmailScheduleRepository emailScheduleRepository;
	
	@Autowired
	private EmailScheduleExcelServiceImpl emailScheduleExcelServiceImpl;
	
	@Autowired
	private EmailSechedulePdfServiceImpl emailSchedulePdfServiceImpl;
	
	@Autowired
	private EmailScheduleCsvServiceImpl emailScheduleCsvServiceImpl;
	
	@Autowired
	private TaskScheduler taskScheduler;
	
	private ScheduledFuture<?> activeTask;
	
	@Autowired
	private GeneralSpecificationUtil<EmailSchedule> generalSpecificationEmailShedule;
	
	public EmailScheduleServiceImpl(TaskScheduler scheduler) {
		this.taskScheduler = scheduler;
	}
	
	@Override
	public List<EmailSchedule> getAll() {
		return emailScheduleRepository.findAllByIsDeletedFalse();
	}

	@Override
	public void save(EmailSchedule emailSchedule) {
		emailSchedule.setDeleted(false);
		this.emailScheduleRepository.save(emailSchedule);
	}

	@Override
	public EmailSchedule getById(long id) {
		Optional<EmailSchedule> optional = emailScheduleRepository.findById(id);
		EmailSchedule emailSchedule = null;
		if (optional.isPresent()) {
			emailSchedule = optional.get();
		} else {
			throw new RuntimeException(EmailScheduleConstants.EMAIL_SCHEDULE_NOT_FOUND + id);
		}
		return emailSchedule;
	}

	@Override
	public void deletedById(long id) {
		Optional<EmailSchedule> optional = emailScheduleRepository.findById(id);
		EmailSchedule emailSchedule = null;
		if (optional.isPresent()) {
			emailSchedule = optional.get();
			emailSchedule.setDeleted(true);
		} else {
			throw new RuntimeException(EmailScheduleConstants.EMAIL_SCHEDULE_NOT_FOUND + id);
		}
		this.emailScheduleRepository.save(emailSchedule);
	}
	
	 public void sendFileFromMail(EmailSchedule emailSchedule, HttpServletResponse response) {
		 try {
			//Scheduler Start
			 Runnable task = new Runnable() {
				 public void run() {
					if(ApplicationConstants.FORMAT_PDF.equalsIgnoreCase(emailSchedule.getFileType())) {
						 String fileName=ApplicationConstants.DELIMITER_EMPTY;
						 try {
								
							 fileName = emailSchedulePdfServiceImpl.export();
							 sendEmail(emailSchedule, fileName);
							
						} catch (Exception e) {
							 e.printStackTrace();
						}
					}
					else if(ApplicationConstants.FORMAT_CSV.equalsIgnoreCase(emailSchedule.getFileType())) {
						String fileName=ApplicationConstants.DELIMITER_EMPTY;
						try {
							fileName = emailScheduleCsvServiceImpl.csvGenerator();
							sendEmail(emailSchedule, fileName);
						}  catch (Exception e) {
							e.printStackTrace();
						}
						
					}
					else if(ApplicationConstants.FORMAT_EXCEL.equalsIgnoreCase(emailSchedule.getFileType())) {
						String fileName=ApplicationConstants.DELIMITER_EMPTY;
						try {
							fileName = emailScheduleExcelServiceImpl.emailSchedular();
							sendEmail(emailSchedule, fileName);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}
				}
			};

			Date dateTime = emailSchedule.getSendTiming();
		
			String cronValue = toCron(String.valueOf(dateTime.getSeconds()),String.valueOf(dateTime.getMinutes()),String.valueOf(dateTime.getHours()),dateTime);
			activeTask=taskScheduler.schedule(task, new CronTrigger(cronValue));
//			taskScheduler.scheduleAtFixedRate(task, dateTime, 24);
//			scheduler end
			
//			ByteArrayInputStream dailyReportExcel = emailSchedular();
//			sendEmail(session, toEmail, emailSchedule.getEmailSubject(), emailSchedule.getEmailBody(), dailyReportExcel);
			
//			DataSource fds = emailSchedular();
//			sendEmail(session, toEmail, emailSchedule.getEmailSubject(), emailSchedule.getEmailBody(), fds);
			
		 }catch(Exception e) {
			 e.printStackTrace();
		 }
	}
	
	 public void removeTaskFromScheduler() {
		
		if (activeTask != null) {
			activeTask.cancel(true);
		}
	}
	
	private static void sendEmail(EmailSchedule emailSchedule, String fileName) throws Exception{
		try {
			final String subject = emailSchedule.getEmailSubject();
			final String body = emailSchedule.getEmailSubject();
			final String fromEmail = EmailScheduleConstants.FROM_EMAIL;
			final String password = EmailScheduleConstants.PASSWORD;
			final String toEmail = emailSchedule.getToEmailAddress();
			
			//System.out.println("TLSEmail Start");
			Properties props = setEmailProperties();
			
	        //create Authenticator object to pass in Session.getInstance argument
			Authenticator auth = new Authenticator() {
				//override the getPasswordAuthentication method
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(fromEmail, password);
				}
			};
			Session session = Session.getInstance(props, auth);
			MimeMessage msg = new MimeMessage(session);
			
			setMimeMessage(fileName, subject, body, toEmail, msg);
			
			Transport.send(msg);
	    }
		catch (AddressException e) {
            throw new AddressException(EmailScheduleConstants.INCORRECT_EMAIL_ADDRESS);

        } catch (MessagingException e) {
            throw new MessagingException(EmailScheduleConstants.AUTHENTICATION_FAILED);

        } catch (Exception e) {
            throw  new Exception(EmailScheduleConstants.ERROR_IN_METHOD + e.getMessage());
        }
	}

	private static void setMimeMessage(String fileName, final String subject, final String body, final String toEmail,
			MimeMessage msg) throws MessagingException, AddressException {
		//set message headers
		msg.addHeader(ApplicationConstants.HEADER_CONTENT_TYPE, EmailScheduleConstants.TEXT_OR_HTML);
		msg.addHeader(EmailScheduleConstants.FORMAT, EmailScheduleConstants.FLOWED);
		msg.addHeader(EmailScheduleConstants.CONTENT_TRANSFER_ENCODING, EmailScheduleConstants.EIGHT_BIT);
		
		msg.setFrom(new InternetAddress(EmailScheduleConstants.FROM_EMAIL));
		
		msg.setReplyTo(InternetAddress.parse(EmailScheduleConstants.FROM_EMAIL, false));
		  
		InternetAddress[] myBccList = InternetAddress.parse(EmailScheduleConstants.BCC_EMAIL);
		InternetAddress[] myCcList = InternetAddress.parse(EmailScheduleConstants.CC_EMAIL);
		msg.setRecipients(Message.RecipientType.BCC, myBccList);
		msg.setRecipients(Message.RecipientType.CC, myCcList);

		// Set Subject: header field
		msg.setSubject(subject);

		// Create the message part 
		BodyPart messageBodyPart = new MimeBodyPart();

		// Fill the message
		messageBodyPart.setText(body);

		// Create a multipar message
		Multipart multipart = new MimeMultipart();

		// Set text message part
		multipart.addBodyPart(messageBodyPart);

		// Part two is attachment
		messageBodyPart = new MimeBodyPart();
		DataSource source = new FileDataSource(fileName);
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(fileName);
		multipart.addBodyPart(messageBodyPart);

		// Send the complete message parts
		msg.setContent(multipart);

		msg.setSentDate(new Date());

		msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
	}

	private static Properties setEmailProperties() {
		Properties props = new Properties();
		props.put(EmailScheduleConstants.MAIL_SMTP_HOST, EmailScheduleConstants.SMTP_GMAIL_COM); //SMTP Host
		props.put(EmailScheduleConstants.MAIL_SMTP_PORT, NumberConstants.FIVE_HUNDRED_EIGHTY_SEVEN); //TLS Port
		props.put(EmailScheduleConstants.MAIL_SMTP_AUTH, ApplicationConstants.TRUE); //enable authentication
		props.put(EmailScheduleConstants.MAIL_SMTP_START_TLS_ENABLE, ApplicationConstants.TRUE); //enable STARTTLS
		return props;
	}
		
	private static String toCron(final String sec, final String mins, final String hrs, Date dateTime) {
		
		final String day = String.valueOf(dateTime.getDate());
		final String month = String.valueOf(dateTime.getMonth() + NumberConstants.ONE);
		final String dayOfMonth = day+ApplicationConstants.DELIMITER_FORWARD_SLASH +month;
		
		return String.format(ApplicationConstants.DELIMITER_FORMAT_STRING+ApplicationConstants.DELIMITER_FORMAT_STRING+ApplicationConstants.DELIMITER_FORMAT_STRING+ApplicationConstants.DELIMITER_FORMAT_STRING+
        		ApplicationConstants.DELIMITER_FORMAT_STRING+ApplicationConstants.DELIMITER_FORMAT_STRING, sec, mins, hrs,dayOfMonth, ApplicationConstants.DELIMITER_STAR, ApplicationConstants.DELIMITER_STAR);
		
	}

	@Override
	public PaginationDto<EmailSchedule> searchByField(Long id, String reportType, String fileType, String toMail,
			String subject, int pageno, String sortField, String sortDir,String org) {
		if (null == sortDir || sortDir.isEmpty()) {
			sortDir = ApplicationConstants.ASC;
		}
		if (null == sortField || sortField.isEmpty()) {
			sortField = ApplicationConstants.ID;
		}
		Page<EmailSchedule> page = getEmailSchedulePage(id, reportType, fileType, toMail, subject, pageno, sortField,
				sortDir,org);
        
    	List<EmailSchedule> emailScheduleList =  page.getContent();
		
		sortDir = (ApplicationConstants.ASC.equalsIgnoreCase(sortDir))?ApplicationConstants.DESC:ApplicationConstants.ASC;
		PaginationDto<EmailSchedule> dtoList = new PaginationDto<EmailSchedule>(emailScheduleList, page.getTotalPages(),
				page.getNumber() +  NumberConstants.ONE, page.getSize(), page.getTotalElements(), page.getTotalElements(), sortDir, ApplicationConstants.SUCCESS, ApplicationConstants.MSG_TYPE_S);
		return dtoList;
	}

	private Page<EmailSchedule> getEmailSchedulePage(Long id, String reportType, String fileType, String toMail,
			String subject, int pageno, String sortField, String sortDir, String organization) {
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(pageno - NumberConstants.ONE, NumberConstants.TEN, sort);
		
		Specification<EmailSchedule> idSpc = generalSpecificationEmailShedule.longSpecification(id, ApplicationConstants.ID);
		Specification<EmailSchedule> reportTypeSpc = generalSpecificationEmailShedule.stringSpecification(reportType, EmailScheduleConstants.REPORT_TYPE);
		Specification<EmailSchedule> fileTypeSpc = generalSpecificationEmailShedule.stringSpecification(fileType, EmailScheduleConstants.FILE_TYPE);
		Specification<EmailSchedule> toMailSpc = generalSpecificationEmailShedule.stringSpecification(toMail, EmailScheduleConstants.TO_EMAIL_ADDRESS);
		Specification<EmailSchedule> subjectSpc = generalSpecificationEmailShedule.stringSpecification(subject, EmailScheduleConstants.EMAIL_SUBJECT);
		Specification<EmailSchedule> isDeletedFalse = generalSpecificationEmailShedule.booleanSpecification(false, ApplicationConstants.IS_DELETED);
		Specification<EmailSchedule> orgSpc = generalSpecificationEmailShedule.foreignKeyStringSpecification(organization, "organization", ApplicationConstants.NAME);
		
    	Page<EmailSchedule> page = emailScheduleRepository.findAll(idSpc.and(reportTypeSpc).and(isDeletedFalse).and(fileTypeSpc).and(toMailSpc).and(subjectSpc).and(orgSpc), pageable);
		return page;
	}
}

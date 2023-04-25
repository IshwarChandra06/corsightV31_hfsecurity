package com.eikona.mata.entity;



import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.GenericGenerator;

@Entity(name="constarint_single")
public class ConstraintSingle  implements Serializable{
	
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(
            name = "native",
            strategy = "native"
    )
    private Long id;

    @NotBlank(message="Please provide a unique value.")
    @Column(name = "val", unique = true)
    private String value;
    
    @NotBlank(message="Please provide a unique type.")
    @Column(name = "type", unique = true, nullable = false)
    private String type;
    
    @Column(name = "is_deleted")
    private boolean isDeleted;

    public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ConstraintSingle() {
		super();
	}

	public ConstraintSingle(@NotBlank(message = "Please provide a unique value.") String value,
			@NotBlank(message = "Please provide a unique type.") String type, boolean isDeleted) {
		super();
		this.value = value;
		this.type = type;
		this.isDeleted = isDeleted;
	}
    

}

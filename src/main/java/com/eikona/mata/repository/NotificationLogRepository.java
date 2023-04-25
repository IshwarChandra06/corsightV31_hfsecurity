package com.eikona.mata.repository;


import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

import com.eikona.mata.entity.NotificationLog;

public interface NotificationLogRepository extends DataTablesRepository<NotificationLog, Long>{

}

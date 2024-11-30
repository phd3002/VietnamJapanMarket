package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.entity.ShippingStatus;
import com.ecommerce.g58.repository.ShippingStatusRepository;
import com.ecommerce.g58.service.ShippingStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ShippingStatusServiceImp implements ShippingStatusService {

    private static final Logger logger = LoggerFactory.getLogger(ShippingStatusServiceImp.class);

    public ShippingStatusServiceImp() {
        logger.info("ShippingStatusServiceImp bean is initialized!");
    }

    @Autowired
    private ShippingStatusRepository shippingStatusRepository;


//        @Scheduled(cron = "*/10 * * * * ?") // run every 10s
    @Scheduled(cron = "0 0 0 * * ?") // Run at 12:00 AM every day

    @Override
    public void updateShippingStatus() {
        logger.info("Executing updateShippingStatus scheduled task.");

        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);

        // Find all records where status is 'Delivered' and updated_at is 3 days or older
        List<ShippingStatus> statusesToUpdate = shippingStatusRepository.findByStatusAndUpdatedAtBefore("Delivered", threeDaysAgo);

        if (statusesToUpdate.isEmpty()) {
            logger.info("No statuses to update at this time.");
        } else {
            statusesToUpdate.forEach(status -> {
                logger.info("Updating status for order ID: {}", status.getOrderId().getOrderId());
                status.setStatus("Completed");
                status.setUpdatedAt(LocalDateTime.now());
                shippingStatusRepository.save(status);
            });
            logger.info("Successfully updated {} statuses.", statusesToUpdate.size());
        }
    }

}

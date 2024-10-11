package com.billing_software.billing_software.crons;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SpinUpCronService {

    private final RestTemplate restTemplate;

    public SpinUpCronService() {
        this.restTemplate = new RestTemplate();
    }

    // @Scheduled(cron = "0 */14 7-22 * * *") // Runs every 14 minutes from 7 AM to 10:59 PM
    // public void keepServiceAlive() {
    //     String url = "https://billing-software-be-latest.onrender.com/health-check";
    //     try {
    //         restTemplate.getForEntity(url, String.class);
    //         System.out.println("Called health-check micro service is active for next 15min");
    //     } catch (Exception e) {
    //         System.err.println("Failed to hit the service: " + e.getMessage());
    //     }

    //     String fornt_url = "https://billing-software-fe.onrender.com/";
    //     try {
    //         restTemplate.getForEntity(fornt_url, String.class);
    //         System.out.println("Called front end service is active for next 15min");
    //     } catch (Exception e) {
    //         System.err.println("Failed to hit the service: " + e.getMessage());
    //     }
    // }

}

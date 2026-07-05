package DyProSo2023;


import org.salespointframework.EnableSalespoint;
import org.salespointframework.SalespointSecurityConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Properties;


@EnableSalespoint
@SpringBootApplication
public class DyProSoMain {
    private static final String LOGIN_ROUTE ="/login";

    public static void main(String[] args){
        SpringApplication.run(DyProSo2023.DyProSoMain.class, args);
            }

    @Configuration
    static class WebSecurityConfiguration extends SalespointSecurityConfiguration {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable();  // for lab purposes, that's ok!
            http.authorizeRequests()
                    .antMatchers("/upload").access("hasRole('ROLE_USER')")
                    .antMatchers("/admin").access("hasRole('ROLE_ADMIN')")
                    .antMatchers("/profile").access("hasRole('ROLE_USER')")
                    .antMatchers("/abstract").access("hasRole('ROLE_USER')")
                    .and()
                            .formLogin().loginPage(LOGIN_ROUTE)
                            .loginProcessingUrl(LOGIN_ROUTE)
                            .defaultSuccessUrl("/")
                    .and()
                            .logout().logoutUrl("/logout").logoutSuccessUrl("/login?logout");
        }

        @Bean
        public UserDetailsService userDetailsService() {
            return super.userDetailsService();
        }

        @Bean
        public JavaMailSender getJavaMailSender(){
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost("msx.tu-dresden.de");
            mailSender.setPort(587);

            mailSender.setUsername("dyproso");
            mailSender.setPassword("*******");             

            Properties props = mailSender.getJavaMailProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.debug", "true");

            return mailSender;
        }
    }
}




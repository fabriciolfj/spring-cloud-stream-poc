package com.github.fabriciolfj.cloudstreamsolace;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.binder.BinderHeaders;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@SpringBootApplication
public class CloudStreamSolaceApplication {

	private static final String TOPIC_PREFIX = "tech/officehours/solace/";

	public static void main(String[] args) {
		SpringApplication.run(CloudStreamSolaceApplication.class, args);
	}

	//usando message builder
	@Bean
	public Function<DeveloperSession, Message<DeveloperSession>> uppercase() {
		return ds -> {
			log.info("Uppercasing session: " + ds);
			final var developer = new DeveloperSession();
			developer.setRegion(ds.getRegion().toUpperCase());
			developer.setTitle(ds.getTitle().toUpperCase());

			String startTime = ds.getUtcstarttime();
			developer.setUtcendtime(ds.getUtcendtime());
			developer.setUtcstarttime(startTime);

			String topic = TOPIC_PREFIX + startTime.substring(0, startTime.indexOf("T"));
			return MessageBuilder.withPayload(developer).setHeader(BinderHeaders.TARGET_DESTINATION, topic)
					.build();
		};
	}

	//Consome e produz
	/*@Bean
	public Function<DeveloperSession, DeveloperSession> uppercase() {
		return ds -> {
			log.info("Uppercasing Session: " + ds);
			final var developer = new DeveloperSession();
			developer.setRegion(ds.getRegion().toUpperCase());
			developer.setTitle(ds.getTitle().toUpperCase());
			developer.setUtcendtime(ds.getUtcendtime());
			developer.setUtcstarttime(ds.getUtcstarttime());
			return developer;
		};
	}*/

	//publicando usando streambridge
	/*@Bean
	public Consumer<DeveloperSession> uppercase(StreamBridge sb) {
		return ds -> {
			log.info("Uppercasing session: " + ds);
			final var developer = new DeveloperSession();
			developer.setRegion(ds.getRegion().toUpperCase());
			developer.setTitle(ds.getTitle().toUpperCase());

			String startTime = ds.getUtcstarttime();
			developer.setUtcendtime(ds.getUtcendtime());
			developer.setUtcstarttime(startTime);

			String topic = TOPIC_PREFIX + startTime.substring(0, startTime.indexOf("T"));
			sb.send(topic, developer);
		};
	}*/
	
	@Data
	@NoArgsConstructor
	static class DeveloperSession {
		private String title, region, utcstarttime, utcendtime;
	}

}

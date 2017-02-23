package org.krendel.test;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import java.util.List;

@SpringBootApplication
@EnableRedisRepositories
public class AnagramsApplication {
    @Value("${redis.hostName}")
	private String redisHostName;

	@Value("${redis.port}")
	private int redisPort;

	@Value("${redis.password}")
	private String redisPassword;


	public static void main(String[] args) {
		SpringApplication.run(AnagramsApplication.class, args);
	}

	@Bean
	public RedisConnectionFactory connectionFactory() {
		JedisConnectionFactory factory = new JedisConnectionFactory();
		factory.setHostName(redisHostName);
		factory.setPort(redisPort);
		factory.setPassword(redisPassword);
		return factory;
	}

	@Bean
	public RedisTemplate<String, List<String>> redisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, List<String>> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);

		return template;
	}
}

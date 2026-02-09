package rs.ac.uns.ftn.asd.ProjekatSIIT2025.websockets.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisConfig {
    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                                   MessageListenerAdapter vehicleListener,
                                                   MessageListenerAdapter rideListener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(vehicleListener, new PatternTopic("vehicle-locations"));
        container.addMessageListener(rideListener, new PatternTopic("ride-updates"));
        return container;
    }

    @Bean
    public MessageListenerAdapter vehicleListener(RedisReceiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveVehicleMessage");
    }

    @Bean
    public MessageListenerAdapter rideListener(RedisReceiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveRideMessage");
    }
}
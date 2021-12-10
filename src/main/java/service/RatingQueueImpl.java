package service;

import repository.entity.Rating;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.jms.*;
import java.util.Optional;

@ApplicationScoped
public class RatingQueueImpl implements RatingQueue {
    @Resource(name = "RatingQueue")
    private Queue ratingQueue;

    @Resource
    private ConnectionFactory connectionFactory;

    private Connection connection;

    private Session session;

    private QueueBrowser queueBrowser;

    static {
        // ObjectMessage.getObject() throws JMS exception if the package is not set as serializable.
        // Read more: http://activemq.apache.org/objectmessage.html
        System.setProperty("org.apache.activemq.SERIALIZABLE_PACKAGES", "*");
    }

    @PostConstruct
    public void init() {
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            queueBrowser = session.createBrowser(ratingQueue);
            connection.start();
        } catch (JMSException e) {
            throw new RuntimeException("Caught JMS exception while setting up connection.");
        }
    }

    @PreDestroy
    public void close() {
        try {
            connection.close();
        } catch (JMSException e) {
            throw new RuntimeException("Caught JMS exception while closing connection.");
        }
    }

    @Override
    public void sendRating(Rating rating) {
        try(MessageProducer producer = session.createProducer(ratingQueue)) {

            ObjectMessage ratingMsg = session.createObjectMessage(rating);
            producer.send(ratingMsg);

        } catch (JMSException e) {
            throw new RuntimeException("Caught JMS exception while sending student.", e);
        }
    }

    @Override
    public Optional<Rating> retrieveNextRating() {
        try(MessageConsumer consumer = session.createConsumer(ratingQueue)) {

            ObjectMessage ratingMsg = (ObjectMessage) consumer.receive(1000);
            return Optional.ofNullable((Rating) ratingMsg.getObject());

        } catch (JMSException e) {
            throw new RuntimeException("Caught JMS exception while getting student.", e);
        }
    }
}

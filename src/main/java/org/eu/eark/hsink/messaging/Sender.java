package org.eu.eark.hsink.messaging;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eu.eark.hsink.properties.ConfigProperties;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;


public class Sender {
	
	private final static Logger LOG = Logger.getLogger(Sender.class.getName());
	
	private String hostname = "localhost";
	private String queuename = "file-ingest";
	private String exchange = "";
	
	private ConnectionFactory factory = null;
	
  protected void init() {

    String hostname = ConfigProperties.getInstance().getProperty("HOSTNAME");
    if(hostname != null) {
    	this.hostname = hostname;
    	LOG.log(Level.FINE, "RabbitMQ hostname set to "+this.hostname);
    } else {
    	LOG.log(Level.FINE, "RabbitMQ hostname not set defaulting to "+this.hostname);
    }
    
    String queuename = ConfigProperties.getInstance().getProperty("QUEUENAME");
    if(queuename != null) {
    	this.queuename = queuename;
    	LOG.log(Level.FINE, "RabbitMQ queuename set to "+this.queuename);
    } else {
    	LOG.log(Level.FINE, "RabbitMQ queuename not set defaulting to "+this.queuename);
    }    	
    
    String exchange = ConfigProperties.getInstance().getProperty("EXCHANGE");
    if(exchange != null) {
    	this.exchange = exchange;
    	LOG.log(Level.INFO, "RabbitMQ exchange set to "+this.exchange);
    }
  }
  
  public Sender() {
  	init();
  	factory = new ConnectionFactory();
    factory.setHost(hostname);

  }
  
  public Sender(String hostname, String exchange, String routingKey) {
  	this.hostname = hostname;
  	this.queuename = routingKey;
  	this.exchange = exchange;
  }
	
  public void sendMessage(String message) throws IOException, TimeoutException {
  	try {
  		Connection connection = factory.newConnection();
  		Channel channel = connection.createChannel();
  		byte[] messageBodyBytes = message.getBytes();
  		channel.basicPublish(exchange, queuename, null, messageBodyBytes);
  		LOG.log(Level.INFO, "Message sent to message broker: "+message);
  		channel.close();
  		connection.close();
  	} catch(Exception ex) {
    	LOG.log(Level.WARNING, "Failed to send message: "+message);
    	LOG.log(Level.WARNING, ""+ex);
  	}
  }

}

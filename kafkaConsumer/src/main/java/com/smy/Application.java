package com.smy;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.serializer.StringDecoder;
import kafka.utils.VerifiableProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Application {

    private final ConsumerConnector consumer;

    private final static  String TOPIC="mytopic";

    private Application(){
        Properties props=new Properties();
        //zookeeper
        props.put("zookeeper.connect","192.168.1.108:2181");
        //topic
        props.put("group.id","mytopic");

        //Zookeeper 超时
        props.put("zookeeper.session.timeout.ms", "4000");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");
        props.put("auto.offset.reset", "smallest");


        props.put("serializer.class", "kafka.serializer.StringEncoder");

        ConsumerConfig config=new ConsumerConfig(props);

        consumer= kafka.consumer.Consumer.createJavaConsumerConnector(config);
    }


    void consume(){
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(TOPIC, new Integer(1));

        StringDecoder keyDecoder = new StringDecoder(new VerifiableProperties());
        StringDecoder valueDecoder = new StringDecoder(new VerifiableProperties());

        Map<String, List<KafkaStream<String, String>>> consumerMap =
                consumer.createMessageStreams(topicCountMap,keyDecoder,valueDecoder);
        KafkaStream<String, String> stream = consumerMap.get(TOPIC).get(0);
        ConsumerIterator<String, String> it = stream.iterator();

        int messageCount = 1;
        while (it.hasNext()){
            System.out.println(it.next().message());
            messageCount++;
            if(messageCount == 100){
                System.out.println("Consumer端一共消费了" + messageCount + "条消息！");
            }
        }
    }

    public static void main(String[] args) {
        new Application().consume();
    }

    }


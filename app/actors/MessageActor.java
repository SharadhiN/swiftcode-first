package actors;

import akka.actor.ActorRef; //play library
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.fasterxml.jackson.databind.ObjectMapper;
import data.FeedResponse;
import data.Message;
import services.AgentService;
import services.FeedService;

import java.util.Objects;
import java.util.UUID;

public class MessageActor extends UntypedActor {
   private final ActorRef out;


   public static Props props(ActorRef out)
   {
       return Props.create(MessageActor.class, out);
   }

   private FeedService feedService = new FeedService();
   private AgentService newsAgentService = new AgentService();

   public MessageActor(ActorRef out)
   {
       this.out = out;
   }


   @Override public void onReceive(Object message) throws Exception {

       ObjectMapper mapper = new ObjectMapper();             //ObjectMapper, untyped actor are all from PLAY library.
       Message messageObject = new Message();
       if (message instanceof Object) {
           messageObject.text = (String) message;
           messageObject.sender = Message.Sender.USER;
           out.tell(mapper.writeValueAsString(messageObject),
                   self());
           String keyword = newsAgentService.getAgentResponse((String) message).keyword;
           if(!Objects.equals(keyword, "NOT_FOUND")){
               FeedResponse feedResponse = feedService.getFeedResponse(keyword);
               messageObject.text = (feedResponse.title == null) ? "No results found" : "Showing results for: " + keyword;
               messageObject.feedResponse = feedResponse;
               messageObject.sender = Message.Sender.BOT;
               out.tell(mapper.writeValueAsString(messageObject), self());
           }
       }

   }

}

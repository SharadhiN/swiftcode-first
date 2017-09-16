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

public class MessageActor extends UntypedActor
{
    public static Props props(ActorRef out) //Properties of actor
    {
        return Props.create(MessageActor.class, out);
    }

    private final ActorRef out;
    private FeedService feedService = new FeedService();
    private AgentService agentService = new AgentService();

    public MessageActor(ActorRef out)
    {
        this.out = out;
    }

    @Override
    public void onReceive(Object message) throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        Message messageObject = new Message();
        if(message instanceof Object)
        {
            messageObject.text = (String) message;
            messageObject.sender = Message.Sender.USER;
            out.tell(mapper.writeValueAsString(messageObject),
                    self());
            String keyword = String.valueOf(agentService.getAgentResponse((String) message));
            if(!Objects.equals(keyword, "NOT_FOUND"))
            {
                FeedResponse feedResponse = feedService.getFeedResponse(keyword);
                messageObject.text = (feedResponse.title == null) ? "No results found" : "Showing results for: " + keyword;
                messageObject.feedResponse = feedResponse;
                messageObject.sender = Message.Sender.BOT;
                out.tell(mapper.writeValueAsString(messageObject), self());
            }
        }
    }
}

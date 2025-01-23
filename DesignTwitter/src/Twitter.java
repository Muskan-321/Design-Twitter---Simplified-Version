import java.util.*;
import java.util.concurrent.*;
//-----------------------------------------Helper classes-----------------------------------------
class Tweet implements Comparable<Tweet>{
    int time;
    int tweetId;
    Tweet(int t, int tId){
        this.time = t;
        this.tweetId = tId;
    }
    @Override
    public int compareTo(Tweet that) {
        return that.time - this.time;   //Decreasing order by time
    }
}
class User{
    int userId;
    Set<Integer> followers;
    List<Tweet> tweets;
    User(int uId){
        this.userId = uId;
        this.followers = new HashSet<>();
        this.tweets    = new ArrayList<>();
    }

    public void addTweet(Tweet tweet){
        tweets.add(0, tweet);
    }
    public void addFollower(int followeeId){
        followers.add(followeeId);
    }
    public void removeFollower(int followeeId){
        followers.remove(followeeId);
    }
}
//-----------------------------------Twitter Design-----------------------------
public class Twitter {
    private Map<Integer, User> userMap;
    private int timeCounter;

    //Priority task queues
    private ExecutorService highPriorityQueue;
    private ExecutorService lowPriorityQueue;

    public Twitter(){
        userMap = new HashMap<>();
        timeCounter = 0;
        highPriorityQueue = Executors.newFixedThreadPool(5);  //Adjust thread count based on load
        lowPriorityQueue = Executors.newSingleThreadExecutor();  //Single thread for sequential processing
    }

    //----------------------------------Post-Tweet Method---------------------------------------------
    public void postTweet(int userId, int tweetId){
        timeCounter += 1;
        userMap.putIfAbsent(userId, new User(userId));
        User user = userMap.get(userId);
        user.addTweet(new Tweet(timeCounter, tweetId));

        highPriorityQueue.submit(() -> notifyFollowers(userId, tweetId));  //Notify followers of a new tweet;
        lowPriorityQueue.submit(() -> analyzeTweet(tweetId)); //Analyze tweet trends
    }

    // 10 most recent posts are the feed--------------------------------------------------------------
    public List<Integer> getNewsFeed(int userId){
        if(!userMap.containsKey(userId)) return new ArrayList<>();
        PriorityQueue<Tweet> maxHeap = new PriorityQueue<>();
        User user = userMap.get(userId);
        maxHeap.addAll(user.tweets);

        for(int followerId : user.followers){
            if(userMap.containsKey(followerId)){
                maxHeap.addAll(userMap.get(followerId).tweets);
            }
        }
        List<Integer> recentTweets = new ArrayList<>();
        int count = 0;
        while(!maxHeap.isEmpty() && count < 10){
            recentTweets.add(maxHeap.poll().tweetId);
            count++;
        }
        return recentTweets;
    }

//----------------------------------Follow---------------------------------------------
    public void follow(int followerId, int followeeId){
        userMap.putIfAbsent(followerId, new User(followerId));
        userMap.putIfAbsent(followeeId, new User(followeeId));
        userMap.get(followerId).addFollower(followeeId);
    }

//--------------------------------Unfollow----------------------------------------------------
    public void unfollow(int followerId, int followeeId){
        if(!userMap.containsKey(followerId) || !userMap.containsKey(followeeId))
            return;
        userMap.get(followerId).removeFollower(followeeId);
    }

//-------------------------------NotifyFollowers----------------------------------------
    private void notifyFollowers(int userId, int tweetId){
        //Simulating high priority notification task
        User user = userMap.get(userId);
        for(int followerId : user.followers){
            System.out.println("Notifying user " + followerId + " about tweet " +tweetId+ " from user "+userId);
        }
    }

//------------------------------AnalyzeTweet------------------------------------------------
    private void analyzeTweet(int tweetId){
        System.out.println("Analyzing tweet trends for tweet ID : "+tweetId);
    }

//----------------------------Shut Down Queues-----------------------------------
    public void shutDownQueues(){
        highPriorityQueue.shutdown();
        lowPriorityQueue.shutdown();
    }

}

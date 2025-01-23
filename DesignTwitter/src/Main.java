import javax.swing.event.TreeWillExpandListener;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        Twitter twitter = new Twitter();
        twitter.postTweet(1, 5);
        twitter.follow(1, 2);
        twitter.postTweet(2, 6);
        twitter.unfollow(1, 2);
        twitter.follow(3, 5);

        twitter.shutDownQueues();
    }
}
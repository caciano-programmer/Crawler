package crawl;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

class spider 
{   
    //Initialize the frame GUI and its components
    JFrame frame = new JFrame();
    JPanel panel = new JPanel(new BorderLayout());
    JTextArea text = new JTextArea();
    //Results holds desired results, queue holds the websites to be searched
    HashSet<String> results = new HashSet<>();
    ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
    
    //Calls the search method
    void result(String website, String keyword, int duration) 
    {
        frame.setLayout(new BorderLayout());
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);
        frame.setTitle("Please wait approximately " + duration + " seconds.");
        text.setLineWrap(false);
        text.setWrapStyleWord(false);
        text.setEditable(false);
        panel.add(text);
        JScrollPane scroll = new JScrollPane(text, 
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        panel.add(scroll);
        frame.add(panel);
        frame.setVisible(true);
        search(website, keyword, duration);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    //Keep searching until time limit is reached, since the links found on the 
    //first website are likely to be more valuable, thus a thread is made from
    //each link found on the first website, and then each thread does standard
    //web crawling
    private void search(String website, String keyword, int duration) 
    {
        searchHelper(website, keyword);
        int limit = queue.size();
        Thread[] threads = new Thread[limit];
        long start = System.currentTimeMillis();

        for(int i = 0; i < limit; i++)
        {
            threads[i] = new Thread(new Runnable() 
            {
                @Override
                public void run()
                {
                    while(!queue.isEmpty() && System.currentTimeMillis() 
                            - start <= (1000 * duration))
                        searchHelper(queue.poll(), keyword);
                }
            });  
            threads[i].start();
        }
        
        try
        {
            for(Thread thread: threads)
                thread.join();
        } catch(InterruptedException e){}
        
        if(results.isEmpty())
            text.append("No results, sorry :(");
        else
        {
            text.append("\nList of results:\n\n");
            for(String x: results)
                text.append(x + "\n");      
        }
    }

    //The following code makes sure no two threads modify results at the same time
    synchronized private void addResults(String s) { results.add(s); }
    
    synchronized private boolean contains(String s) { return results.contains(s); }
    
    //Grabs links from a website and separates the results if any are found
    void searchHelper(String website, String keyword) 
    { 
        try
        {
            Document doc = Jsoup.connect(website).get();
            Elements links = doc.select("a[href]");
            for(Element x: links)
            {
                String site = x.absUrl("href");
                if(site.matches("(^http)(s?://)(.*)") && !contains(site))
                    queue.add(site);
                if(site.matches("(^http)(s?://)(.*)" + keyword + "(.*)") && 
                        !site.equals(website) && !contains(site))
                        addResults(site);
            }
        }
        catch(IOException e) {}  
    }
}
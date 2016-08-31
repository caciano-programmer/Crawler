package crawl;

import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.apache.commons.validator.routines.UrlValidator;

public class user_interface 
{
    public user_interface() { response(); };
    
    private void response() 
    { 
        JFrame frame = new JFrame();
        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);
        frame.setTitle("Caciano Spider v.1");
        JPanel panel = new JPanel();
        FlowLayout layout = new FlowLayout(FlowLayout.CENTER);
        layout.setVgap(20);
        panel.setLayout(layout);
        JLabel website = new JLabel("Enter Website:");
        JLabel keyword = new JLabel("Enter Keyword:");
        JTextField webField = new JTextField("http://",20);
        webField.setCaretPosition(webField.getDocument().getLength());
        JTextField keyField = new JTextField(20);
        JButton button = new JButton("Submit");
        panel.add(website);
        panel.add(webField);
        panel.add(keyword);
        panel.add(keyField);
        button.addActionListener(x -> search( webField.getText(), 
                keyField.getText() ));
        panel.add(button);
        frame.add(panel);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    private void search(String address, String keyword)
    {
        UrlValidator url = new UrlValidator();
        if(address.equals(""))
            JOptionPane.showMessageDialog(null, "You cannot leave website name"
                    + " blank!", "ERROR" , 
                    JOptionPane.ERROR_MESSAGE);
        else if(!url.isValid(address))
            JOptionPane.showMessageDialog(null, "You have given an invalid URL", 
                    "ERROR" , JOptionPane.ERROR_MESSAGE);
        else
        {
            if(keyword.equals(""))
                keyword = ".";
            int duration = 0;
            String answer = "";
            while( duration < 10 || duration > 200 )
            {
                try
                {
                    answer = JOptionPane.showInputDialog(null, "How many "
                            + "seconds would you like your search\n to last?"
                            + "\nEnter a number between 10 and 200.", 
                            "Search Duration", JOptionPane.QUESTION_MESSAGE);
                    if(answer == null)
                        break;
                    duration = Integer.parseInt(answer);
                    if(duration < 10 || duration > 200)
                    {
                        JOptionPane.showMessageDialog(null, "Please make sure you "
                            + "enter a number between 10 and 200", "ERROR", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch(NumberFormatException e)
                {
                    JOptionPane.showMessageDialog(null, "Please make sure you "
                            + "enter a number between 10 and 200", "ERROR", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            if(answer != null)
            {
                spider showResults = new spider();
                showResults.result(address, keyword, duration);
            }
        }
    }  
}

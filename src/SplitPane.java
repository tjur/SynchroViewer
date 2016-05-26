
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;


public class SplitPane extends JSplitPane
{
    private final Automaton automaton;
    private int K, N;
    
    private PaintPanel paintPanel;
    
    private final TextToolbar textToolbar;
    
    private ArrayList<DockToolbar> dockToolbars = new ArrayList<>();
    
    public SplitPane()
    {
        super(JSplitPane.HORIZONTAL_SPLIT);
        
        setBackground(new Color(224, 224, 224));
        
        automaton = new Automaton("1 0");
        paintPanel = new PaintPanel(automaton);
        setTopComponent(paintPanel);
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
        rightPanel.add(new JScrollPane(innerPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        Dimension rightPanelMinimumSize = new Dimension(300, 0);
        rightPanel.setMinimumSize(rightPanelMinimumSize);
        setBottomComponent(rightPanel);
        setResizeWeight(1.0);
        
        textToolbar = new TextToolbar("Text toolbar", automaton);
        innerPanel.add(textToolbar);
        
        MinSyncWordToolbar minSyncWordToolbar = new MinSyncWordToolbar("Minimal sync word toolbar", automaton);
        innerPanel.add(minSyncWordToolbar);
        
        dockToolbars.add(textToolbar);
        dockToolbars.add(minSyncWordToolbar);
        
        updateToolbars();
        textToolbar.setText("2 4 1 0 3 0 0 1 1 2");
        
        textToolbar.addPropertyChangeListener("repaintAutomaton", new PropertyChangeListener() {
            
            @Override
            public void propertyChange(PropertyChangeEvent ev)
            {
                paintPanel.repaintAutomaton();
            }
        });
        
        textToolbar.addPropertyChangeListener("updateToolbars", new PropertyChangeListener() {
            
            @Override
            public void propertyChange(PropertyChangeEvent ev)
            {
                updateToolbars();
            }
        });
        
        textToolbar.addPropertyChangeListener("updateAndRepaintAutomaton", new PropertyChangeListener() {
            
            @Override
            public void propertyChange(PropertyChangeEvent ev)
            {
                paintPanel.updateAutomatonData();
                paintPanel.repaintAutomaton();
            }
        });
        
        innerPanel.addContainerListener(new ContainerListener() {

            @Override
            public void componentAdded(ContainerEvent e)
            {
                if (innerPanel.getComponents().length == 1)
                {
                    rightPanel.setMinimumSize(rightPanelMinimumSize);
                    SplitPane.this.setDividerLocation(-1);
                    SplitPane.this.setEnabled(true);
                }
            }

            @Override
            public void componentRemoved(ContainerEvent e)
            {
                if (innerPanel.getComponents().length == 0)
                {
                    rightPanel.setMinimumSize(new Dimension(0, 0));
                    SplitPane.this.setDividerLocation(1.0);
                    SplitPane.this.setEnabled(false);                   
                }
            }
        });
        
        for (DockToolbar dockToolbar : dockToolbars)
        {
            dockToolbar.addPropertyChangeListener("setVisible", new PropertyChangeListener() {
            
                @Override
                public void propertyChange(PropertyChangeEvent ev)
                {
                    int visibleToolbars = 0;
                    for (DockToolbar dt : dockToolbars)
                    {
                        if (dt.isVisible())
                            visibleToolbars++;
                    }
                    
                    if (visibleToolbars == 0)
                    {
                        rightPanel.setMinimumSize(new Dimension(0, 0));
                        SplitPane.this.setDividerLocation(1.0);
                        SplitPane.this.setEnabled(false);                
                    }
                    else if (visibleToolbars == 1 && (boolean) ev.getNewValue())
                    {
                        rightPanel.setMinimumSize(rightPanelMinimumSize);
                        SplitPane.this.setDividerLocation(-1);
                        SplitPane.this.setEnabled(true);                        
                    }
                }
            });
        }
    }
    
    private void updateToolbars()
    { 
        for (DockToolbar dockToolbar : dockToolbars)
            dockToolbar.updateToolbar();
    }
    
    public int getAutomatonK()
    {
        return automaton.getK();
    }
    
    public String getAutomatonString()
    {
        return automaton.toString();
    }
    
    public PaintPanel getPaintPanel()
    {
        return paintPanel;
    }
    
    public TextToolbar getTextPanel()
    {
        return textToolbar;
    }
    
    public ArrayList<DockToolbar> getDockToolbars()
    {
        return dockToolbars;
    }
}

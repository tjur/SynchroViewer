
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;


public class Automaton
{

    private int K, N; // max number of out edges for one state / number of states
    private int [][] matrix;
    private ArrayList<Integer> selectedStates;
    
    private final PropertyChangeSupport PCS;

    public Automaton(String code) throws IllegalArgumentException 
    {
        PCS = new PropertyChangeSupport(this);
        
        // Parse code
        String[] tokens = code.trim().replace('\n', ' ').split("\\s+");
        if (tokens.length < 2)
            throw new IllegalArgumentException("Invalid automaton code (expected K N).");
        
        K = Integer.parseInt(tokens[0]);
        N = Integer.parseInt(tokens[1]);
        
        if (tokens.length != 2 + K * N)
            throw new IllegalArgumentException("Invalid automaton code (expected " + (2 + K * N) + " numbers but read " + tokens.length + ").");
        
        matrix = new int[N][K];
        for (int n = 0; n < N; n++)
        {
            for (int k = 0; k < K; k++)
            {
                matrix[n][k] = Integer.parseInt(tokens[2 + n * K + k]);
                if (matrix[n][k] < 0 || matrix[n][k] >= N)
                {
                    throw new IllegalArgumentException("Invalid automaton code (number "
                            + tokens[2 + n * K + k] + " at position " + (2 + n * K + k) + " is outside the range [0," + (N - 1) + "])");
                }
            }
        }
        
        selectedStates = new ArrayList<>();
    }
    
    @Override
    public String toString()
    {
        String str = Integer.toString(K) + " " + Integer.toString(N) + " ";
        for (int n = 0; n < N; n++)
        {
            for (int k = 0; k < K; k++)
                str += Integer.toString(matrix[n][k]) + " ";
        }
        
        return str.trim();
    }
    
    public int getK()
    {
        return K;
    }
    
    public int getN()
    {
        return N;
    }
    
    public int[][] getMatrix()
    {
        return matrix;
    }
    
    public void update(Automaton automaton)
    {
        this.K = automaton.K;
        this.N = automaton.N;
        this.matrix = automaton.matrix;
        this.selectedStates = new ArrayList<>();
    }
    
    public void addState()
    {
        int[][] temp = new int[N+1][K];
        for (int n = 0; n < N; n++)
            System.arraycopy(matrix[n], 0, temp[n], 0, K);
        
        for (int k = 0; k < K; k++)
            temp[N][k] = N;
        
        matrix = temp;
        N++;
        selectState(N - 1);
        
        automatonChanged();
    }
    
    public void removeState(int state)
    {
        int[][] temp = new int[N-1][K];
        for (int n = 0; n < N - 1; n++)
        {
            int z = (n < state) ? n : n + 1;
            temp[n] = matrix[z];
            for(int k = 0; k < K; k++)
            {
                if (temp[n][k] == state)
                    temp[n][k] = n;
                else if (temp[n][k] > state)
                    temp[n][k]--;
            }
        }
        
        matrix = temp;
        N--;
        
        for (int i = 0; i < selectedStates.size(); i++)
        {
            if (selectedStates.get(i) > state)
                selectedStates.set(i, selectedStates.get(i) - 1);
        }

        unselectState(state);
    }
    
    public void replaceStates(int state1, int state2)
    {
        for (int i = 0; i < N; i++)
        {
            for (int j = 0; j < K; j++)
            {
                if (matrix[i][j] == state1)
                    matrix[i][j] = state2;
                else if (matrix[i][j] == state2)
                    matrix[i][j] = state1;
            }
        }
        
        int[] temp = matrix[state1];
        matrix[state1] = matrix[state2];
        matrix[state2] = temp;
        
        int i1 = selectedStates.indexOf(state1);
        int i2 = selectedStates.indexOf(state2);
        
        if (i1 == -1 && i2 != -1)
            selectedStates.set(i2, state1);
        else if (i1 != -1 && i2 == -1)
            selectedStates.set(i1, state2);
        
        automatonChanged();
    }
    
    public void addTransition(int out, int in, int k)
    {
        if (k < K) // edit transition
            matrix[out][k] = in;
        else if (k == K)// add new transition
        {
            int[][] temp = new int[N][K+1];
            for (int n = 0; n < N; n++)
            {
                System.arraycopy(matrix[n], 0, temp[n], 0, K);
                temp[n][k] = n;
            }
            temp[out][k] = in;
            matrix = temp;
            K++;
        }
        
        automatonChanged();
    }
    
    public void selectState(Integer state)
    {
        selectedStates.add(state);
        automatonChanged();
    }
    
    public void selectStates(ArrayList<Integer> selectedStates)
    {
        this.selectedStates = selectedStates;
        automatonChanged();
    }
    
    public void unselectState(Integer state)
    {
        selectedStates.remove(state);
        automatonChanged();
    }
    
    public boolean isSelected(Integer state)
    {
        return selectedStates.contains(state);
    }
    
    public int getSelectedStatesNumber()
    {
        return selectedStates.size();
    }
    
    public int[] getSelectedStates()
    {
        int[] subset = new int[N];
        Arrays.fill(subset, 0);
        for (int state : selectedStates)
            subset[N - 1 - state] = 1;
        
        return subset;
    }
    
    public void automatonChanged()
    {
        PCS.firePropertyChange("automatonChanged", false, true);
    }  

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener propertyChangeListener)
    {
        PCS.addPropertyChangeListener(propertyName, propertyChangeListener);
    }
}

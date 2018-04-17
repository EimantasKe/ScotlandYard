package uk.ac.bris.cs.scotlandyard.model;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.Arrays;
import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;
import static uk.ac.bris.cs.scotlandyard.model.Colour.BLACK;
import static uk.ac.bris.cs.scotlandyard.model.Ticket.DOUBLE;
import static uk.ac.bris.cs.scotlandyard.model.Ticket.SECRET;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.ObjectUtils;
import uk.ac.bris.cs.gamekit.graph.Edge;
import uk.ac.bris.cs.gamekit.graph.Graph;
import uk.ac.bris.cs.gamekit.graph.Node;
import uk.ac.bris.cs.gamekit.graph.UndirectedGraph;
import uk.ac.bris.cs.gamekit.graph.ImmutableGraph;
import javax.security.auth.login.Configuration;

//COMS10009 Live Programming Example Code
//Reader: reads undirected graph from file system, simple format
public class Reader
{
    private Graph<Integer,Integer> graph;

    public Graph<Integer,Integer> graph() {
      return graph;
    }
    
    void read(String filename) throws IOException
    {
 	
    	// initialise the graph
    	graph = new UndirectedGraph<Integer,Integer>();
    	
    	// load the file
        File file = new File(filename);
        Scanner in = new Scanner(file);
        
        // get the top line
        //String topLine = in.nextLine();
       // int numberOfNodes = Integer.parseInt(topLine);

        String topLine = in.nextLine();
        String[] numbers = topLine.split(" ");
        int numberOfNodes = Integer.parseInt(numbers[0]);
        int numberOfEdges = Integer.parseInt(numbers[1]);

        
        // create the number of nodes
        for(int i = 0; i < numberOfNodes; i++) {
        	Node<Integer> n = new Node<Integer>(i+1);
        	graph.addNode(n);
        }      
        
        /* read in the graph
        while (in.hasNextLine())
        {
            String line = in.nextLine();
            String[] names = line.split(" ");
            String id1 = names[0];
            String id2 = names[1];
            int mtype;
            if(names[3].equals("LocalRoad")) {
            	mtype = 0;
            } else if(names[3].equals("Underground")) {
            	mtype = 1;
            } else 
            {
            	mtype = 2;
            }

            Edge<Integer,Integer> edge = new Edge<Integer,Integer>(graph.getNode(Integer.parseInt(id1)), 
            		                                               graph.getNode(Integer.parseInt(id2)),new Integer(mtype));
            if (mtype==0) {
            	graph.addEdge(edge);
            }
        }
        */
        for(int i = 0; i < numberOfEdges; i++){
            String line = in.nextLine();
            String[] names = line.split(" ");
            String id1 = names[0];
            String id2 = names[1];
            Integer type;
            if(names[2].equals("Taxi")){
                type = 0;
            }else if(names[2].equals("Bus")){
                type = 1;
            }else if(names[2].equals("Underground")){
                type = 2;
            }else{
                type = 3;
            }
            Edge<Integer,Integer> edge = new Edge<Integer,Integer>(graph.getNode(Integer.parseInt(id1)),graph.getNode(Integer.parseInt(id2)),new Integer(type));
            if (type==0) {
                graph.addEdge(edge);
            }
        }
        in.close();
    }    
}
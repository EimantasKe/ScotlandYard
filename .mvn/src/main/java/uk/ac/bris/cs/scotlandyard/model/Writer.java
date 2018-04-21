package uk.ac.bris.cs.scotlandyard.model;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
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
//Writer: writes graph to file system, simple format
public class Writer {
	private Graph<Integer,Integer> graph;
	
	public void setGraph(Graph<Integer,Integer> graph) {
		this.graph = graph;
	}
	
	public void write(String filename) throws IOException
	{
		// try and write the file
		FileWriter writer = new FileWriter(filename);
		PrintWriter printer = new PrintWriter(writer);
		
		String nodeNumber = Integer.toString(graph.getNodes().size());
		printer.println(nodeNumber);
		
		// now we write all the edges
		List<Edge<Integer,Integer>> edges = new ArrayList<Edge<Integer,Integer>>(graph.getEdges());
		for(Edge<Integer,Integer> e: edges) {		
			String line = e.source().toString() + " " +
					      e.destination().toString() + " 1.0 LocalRoad" ;
			printer.println(line);
		}
		writer.close();
	}
}

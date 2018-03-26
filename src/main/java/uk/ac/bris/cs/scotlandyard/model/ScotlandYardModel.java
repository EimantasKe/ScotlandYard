package uk.ac.bris.cs.scotlandyard.model;

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
import uk.ac.bris.cs.gamekit.graph.ImmutableGraph;
import javax.security.auth.login.Configuration;

// TODO implement all methods and pass all tests
public class ScotlandYardModel implements ScotlandYardGame {
	List<Boolean> rounds;
	private Graph<Integer, Transport> graph;
	List<ScotlandYardPlayer> players;
	private int index = 0;
	private int roundNo = 0;

	public ScotlandYardModel(List<Boolean> rounds, Graph<Integer, Transport> graph,
			PlayerConfiguration mrX, PlayerConfiguration firstDetective,
			PlayerConfiguration... restOfTheDetectives) {

		//testNullRoundsShouldThrow
		this.graph = requireNonNull(graph);
		//testNullMapShouldThrow
		this.rounds = requireNonNull(rounds);
		//testEmptyRoundsShouldThrow
		if (rounds.isEmpty()) {
			throw new IllegalArgumentException("Empty rounds");
		}

		//testEmptyMapShouldThrow
		if (graph.isEmpty()) {
			throw new IllegalArgumentException("Empty graph");
		}

		//testSwappedMrXShouldThrow
		//testNoMrXShouldThrow
		if (mrX.colour != BLACK) { // or mr.colour.isDetective()
			throw new IllegalArgumentException("MrX should be Black");
		}
		//testNullMrXThrow, testAnyNullDetectiveShouldThrow, testNullDetectiveShouldThrow
		ArrayList<PlayerConfiguration> configurations = new ArrayList<>();
		for (PlayerConfiguration configuration : restOfTheDetectives)
			configurations.add(requireNonNull(configuration));
		configurations.add(0, firstDetective);
		configurations.add(0, mrX);

		//testLocationOverlapBetweenDetectivesShouldThrow
		//testLocationOverlapBetweenMrXAndDetectiveShouldThrow
		Set<Integer> set1 = new HashSet<>();
		for (PlayerConfiguration configuration : configurations) {
			if (set1.contains(configuration.location))
				throw new IllegalArgumentException("Duplicate location");
			set1.add(configuration.location);
		}

		Set<Colour> set2 = new HashSet<>();
		for (PlayerConfiguration configuration : configurations) {
			if (set2.contains(configuration.colour))
				throw new IllegalArgumentException("Duplicate colour");
			set2.add(configuration.colour);
		}

		for(PlayerConfiguration configuration : configurations) {
			if(configuration.tickets.get(DOUBLE) != null && configuration.tickets.get(SECRET) != null) {
				if (!configuration.colour.equals(BLACK) && configuration.tickets.get(DOUBLE).compareTo(0) > 0)
					throw new IllegalArgumentException("DetectiveHaveDoubleTicket");
				if (!configuration.colour.equals(BLACK) && configuration.tickets.get(SECRET).compareTo(0) > 0)
					throw new IllegalArgumentException("DetectiveHaveSecretTicket");
				if (configuration.colour.equals(BLACK) && !(configuration.tickets.get(DOUBLE).compareTo(0) > 0))
					throw new IllegalArgumentException("MrXMissingAnyTickets");
				if (configuration.colour.equals(BLACK) && !(configuration.tickets.get(SECRET).compareTo(0) > 0))
					throw new IllegalArgumentException("MrXMissingAnyTickets");
			}
			for (Ticket ticket : Ticket.values()) {
				if (configuration.tickets.get(ticket) != null) {
					if (!(configuration.tickets.get(ticket).compareTo(0) > 0) && !ticket.equals(SECRET) && !ticket.equals(DOUBLE)) {
						throw new IllegalArgumentException("DetectiveMissingAnyTickets && MrXMissingAnyTickets");
					}
				}else{
					throw new IllegalArgumentException("tickets are null");
				}

			}
		}


		players = new ArrayList<>();
		for(PlayerConfiguration configuration : configurations){
			this.players.add(new ScotlandYardPlayer(configuration.player,configuration.colour,configuration.location,configuration.tickets));
		}
	}

	@Override
	public void registerSpectator(Spectator spectator) {
		// TODO
		throw new RuntimeException("Implement me");
	}

	@Override
	public void unregisterSpectator(Spectator spectator) {
		// TODO
		throw new RuntimeException("Implement me");
	}

	@Override
	public void startRotate() {
		// TODO
		throw new RuntimeException("Implement me");
	}

	@Override
	public Collection<Spectator> getSpectators() {
		// TODO
		throw new RuntimeException("Implement me");
	}

	@Override
	public List<Colour> getPlayers() {
		List<Colour> list = new ArrayList<>();
		List<ScotlandYardPlayer> playersImmutable = players;
		for (ScotlandYardPlayer player : playersImmutable) {
			list.add(player.colour());
		}
		return Collections.unmodifiableList(list);
	}

	@Override
	public Set<Colour> getWinningPlayers() {
		// TODO
		throw new RuntimeException("Implement me");
	}

	@Override
	public Optional<Integer> getPlayerLocation(Colour colour) {
		// TODO
		throw new RuntimeException("Implement me");
	}

	@Override
	public Optional<Integer> getPlayerTickets(Colour colour, Ticket ticket) {
		// TODO
		throw new RuntimeException("Implement me");
	}

	@Override
	public boolean isGameOver() {
		// TODO
		//List<> currentPlayer = getPlayerLocation();
		throw new RuntimeException("Implement me");
	}

	@Override
	public Colour getCurrentPlayer() {
		/*int temp = index;
		index = index++;
		if(index == players.size()){
			index = 0;
		}*/
		return players.get(index).colour();
	}

	@Override
	public int getCurrentRound() {
		/*if(roundNo){
			roundNo = 0;
		}
		if(getCurrentPlayer().equals(BLACK)){
			roundNo++;
		}*/
		return roundNo;
	}

	@Override
	public List<Boolean> getRounds() {
		return Collections.unmodifiableList(rounds);
	}

	@Override
	public Graph<Integer, Transport> getGraph() {
		return new ImmutableGraph<>(graph);
	}

}

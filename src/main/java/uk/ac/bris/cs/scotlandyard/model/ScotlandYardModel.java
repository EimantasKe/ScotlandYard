package uk.ac.bris.cs.scotlandyard.model;

import java.util.Arrays;

import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;
import static uk.ac.bris.cs.scotlandyard.model.Colour.BLACK;
import static uk.ac.bris.cs.scotlandyard.model.Ticket.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import com.google.common.collect.ImmutableList;
//import com.sun.org.apache.xalan.internal.xsltc.runtime.Node;
import org.apache.commons.lang3.ObjectUtils;
import uk.ac.bris.cs.gamekit.graph.Edge;
import uk.ac.bris.cs.gamekit.graph.Graph;
import uk.ac.bris.cs.gamekit.graph.ImmutableGraph;
import javax.security.auth.login.Configuration;

// TODO implement all methods and pass all tests
public class ScotlandYardModel implements ScotlandYardGame, Consumer<Move> {
	List<Boolean> rounds;
	private Graph<Integer, Transport> graph;
	List<ScotlandYardPlayer> players;
	private int index = 0;
	private int roundNo = 0;
	private Set<Move> moves = new HashSet<>();
	private final List<Spectator> spectators = new ArrayList<>();
	private int lastKnownLocation;

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
				if (configuration.colour.equals(BLACK) && !(configuration.tickets.get(DOUBLE).compareTo(0) > 0)) {
					throw new IllegalArgumentException("MrXMissingAnyTickets");
				}
				if (configuration.colour.equals(BLACK) && !(configuration.tickets.get(SECRET).compareTo(0) > 0)) {
					throw new IllegalArgumentException("MrXMissingAnyTickets");
				}
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
		requireNonNull(spectator);
		for(Spectator spectato : spectators){
			if(spectato.equals(spectator)){
				throw new IllegalArgumentException();
			}
		}
		this.spectators.add(spectator);
	}

	@Override
	public void unregisterSpectator(Spectator spectator) {
		requireNonNull(spectator);
		boolean duplicate = false;
		for(Spectator spectato : spectators){
			if(spectato.equals(spectator)){
				duplicate = true;
			}
		}
		if(!duplicate) throw new IllegalArgumentException();
		this.spectators.remove(spectator);
	}

	@Override
	public void startRotate() {
		this.moves = validMove(getCurrentPlayer());
		//System.out.println(players.get(players.indexOf(getCurrentPlayer())).location());
		//for(Move movez : moves){ System.out.println(movez.toString()); }
		for(ScotlandYardPlayer player : players){
			if(player.colour().equals(getCurrentPlayer())){
				player.player().makeMove(this, player.location(), moves, this);
				index++;
				if(index == players.size()){
					index=0;
				}
				//if(player.colour().equals(players.get(players.size()-1).colour())){ roundNo++; }
				if(player.isMrX()){ roundNo++; }
			}
		}
	}
	//TODO: check if how many moves left = round.length() - roundNo
	private Set<Move> validMove(Colour player) {
		//TODO: check for rounds
		//TODO: check for tickets
		//TODO: only mr.x can double move


		Set<Move> validMoves = new HashSet<>();
		List<Integer> occoupied = new ArrayList<>();
		for(ScotlandYardPlayer playz: players) {
			if (!playz.colour().equals(player) && playz.isDetective()) {
				occoupied.add(playz.location());
			}
		}
		ScotlandYardPlayer dummy;
		/*(for (ScotlandYardPlayer playr : players) {
			if (playr.colour().equals(player)) {
				dummy = new ScotlandYardPlayer(playr.player(),playr.colour(), playr.location(),playr.tickets());
			}
			}*/
			dummy = new ScotlandYardPlayer(players.get(index).player(),players.get(index).colour(),players.get(index).location(),players.get(index).tickets());
		//dummy.removeTicket(TAXI);
			for (ScotlandYardPlayer play : players) {
				if (play.colour().equals(player) ) {
					Collection<Edge<Integer, Transport>> edgesFrom = getGraph().getEdgesFrom(getGraph().getNode(play.location()));
					//TODO: add TicketMove
					for (Edge<Integer, Transport> edge : edgesFrom) {
						if(dummy.hasTickets(SECRET) && !occoupied.contains(edge.destination().value()) && dummy.hasTickets(SECRET))
							validMoves.add(new TicketMove(player, SECRET, edge.destination().value()));
						if (edge.data().equals(Transport.TAXI) && dummy.hasTickets(TAXI) && !occoupied.contains(edge.destination().value())&& dummy.hasTickets(TAXI)) {
							validMoves.add(new TicketMove(player, TAXI, edge.destination().value()));
						} else if (fromTransport(edge.data()).equals(BUS) && dummy.hasTickets(BUS) && !occoupied.contains(edge.destination().value())&& dummy.hasTickets(BUS)) {
							validMoves.add(new TicketMove(player, BUS, edge.destination().value()));
						} else if (fromTransport(edge.data()).equals(UNDERGROUND) && dummy.hasTickets(UNDERGROUND) && !occoupied.contains(edge.destination().value())&& dummy.hasTickets(UNDERGROUND)) {
							validMoves.add(new TicketMove(player, UNDERGROUND, edge.destination().value()));
						}
					}

					//TODO: add DoubleMove
					if (play.colour().equals(BLACK)) {
						for (Edge<Integer, Transport> edge : edgesFrom) {
							if (fromTransport(edge.data()).equals(TAXI) && dummy.hasTickets(TAXI) && !occoupied.contains(edge.destination().value())) {
								dummy.removeTicket(TAXI);
								Collection<Edge<Integer, Transport>> edgesFromFrom = getGraph().getEdgesFrom(getGraph().getNode(edge.destination().value()));
								for (Edge<Integer, Transport> edge1 : edgesFromFrom) {
									if(!occoupied.contains(edge1.destination().value())&& dummy.hasTickets(SECRET))
										validMoves.add(new DoubleMove(player, new TicketMove(player, TAXI, edge.destination().value()), new TicketMove(player, SECRET, edge1.destination().value())));
									if (fromTransport(edge1.data()).equals(TAXI) && dummy.hasTickets(TAXI) && !occoupied.contains(edge1.destination().value())) {
										validMoves.add(new DoubleMove(player, new TicketMove(player, TAXI, edge.destination().value()), new TicketMove(player, TAXI, edge1.destination().value())));
									} else if (fromTransport(edge1.data()).equals(BUS) && dummy.hasTickets(BUS) && !occoupied.contains(edge1.destination().value())) {
										validMoves.add(new DoubleMove(player, new TicketMove(player, TAXI, edge.destination().value()), new TicketMove(player, BUS, edge1.destination().value())));
									} else if (fromTransport(edge1.data()).equals(UNDERGROUND) && dummy.hasTickets(UNDERGROUND) && !occoupied.contains(edge1.destination().value())) {
										validMoves.add(new DoubleMove(player, new TicketMove(player, TAXI, edge.destination().value()), new TicketMove(player, UNDERGROUND, edge1.destination().value())));
									} else if (fromTransport(edge1.data()).equals(SECRET) && dummy.hasTickets(SECRET) && !occoupied.contains(edge1.destination().value())) {
										validMoves.add(new DoubleMove(player, new TicketMove(player, TAXI, edge.destination().value()), new TicketMove(player, SECRET, edge1.destination().value())));
									}
								}
								dummy.addTicket(TAXI);
							} else if (fromTransport(edge.data()).equals(BUS) && dummy.hasTickets(BUS) && !occoupied.contains(edge.destination().value())) {
								dummy.removeTicket(BUS);
								Collection<Edge<Integer, Transport>> edgesFromFrom = getGraph().getEdgesFrom(getGraph().getNode(edge.destination().value()));
								for (Edge<Integer, Transport> edge1 : edgesFromFrom) {
									if(!occoupied.contains(edge1.destination().value()) && dummy.hasTickets(SECRET))
										validMoves.add(new DoubleMove(player, new TicketMove(player, BUS, edge.destination().value()), new TicketMove(player, SECRET, edge1.destination().value())));

									if (fromTransport(edge1.data()).equals(TAXI) && play.hasTickets(TAXI) && !occoupied.contains(edge1.destination().value())) {
										validMoves.add(new DoubleMove(player, new TicketMove(player, BUS, edge.destination().value()), new TicketMove(player, TAXI, edge1.destination().value())));
									} else if (fromTransport(edge1.data()).equals(BUS) && play.hasTickets(BUS) && !occoupied.contains(edge1.destination().value())) {
										validMoves.add(new DoubleMove(player, new TicketMove(player, BUS, edge.destination().value()), new TicketMove(player, BUS, edge1.destination().value())));
									} else if (fromTransport(edge1.data()).equals(UNDERGROUND) && play.hasTickets(UNDERGROUND) && !occoupied.contains(edge1.destination().value())) {
										validMoves.add(new DoubleMove(player, new TicketMove(player, BUS, edge.destination().value()), new TicketMove(player, UNDERGROUND, edge1.destination().value())));
									} else if (fromTransport(edge1.data()).equals(SECRET) && play.hasTickets(SECRET) && !occoupied.contains(edge1.destination().value())) {
										validMoves.add(new DoubleMove(player, new TicketMove(player, BUS, edge.destination().value()), new TicketMove(player, SECRET, edge1.destination().value())));
									}
								}
								dummy.addTicket(BUS);
							} else if (fromTransport(edge.data()).equals(UNDERGROUND) && play.hasTickets(UNDERGROUND) && !occoupied.contains(edge.destination().value())) {
								dummy.removeTicket(UNDERGROUND);
								Collection<Edge<Integer, Transport>> edgesFromFrom = getGraph().getEdgesFrom(getGraph().getNode(edge.destination().value()));
								for (Edge<Integer, Transport> edge1 : edgesFromFrom) {
									if(!occoupied.contains(edge1.destination().value()))
										validMoves.add(new DoubleMove(player, new TicketMove(player, UNDERGROUND, edge.destination().value()), new TicketMove(player, SECRET, edge1.destination().value())));
									if (fromTransport(edge1.data()).equals(TAXI) && play.hasTickets(TAXI) && !occoupied.contains(edge1.destination().value())) {
										validMoves.add(new DoubleMove(player, new TicketMove(player, UNDERGROUND, edge.destination().value()), new TicketMove(player, TAXI, edge1.destination().value())));
									} else if (fromTransport(edge1.data()).equals(BUS) && play.hasTickets(BUS) && !occoupied.contains(edge1.destination().value())) {
										validMoves.add(new DoubleMove(player, new TicketMove(player, UNDERGROUND, edge.destination().value()), new TicketMove(player, BUS, edge1.destination().value())));
									} else if (fromTransport(edge1.data()).equals(UNDERGROUND) && play.hasTickets(UNDERGROUND) && !occoupied.contains(edge1.destination().value())) {
										validMoves.add(new DoubleMove(player, new TicketMove(player, UNDERGROUND, edge.destination().value()), new TicketMove(player, UNDERGROUND, edge1.destination().value())));
									} else if (fromTransport(edge1.data()).equals(SECRET) && play.hasTickets(SECRET) && !occoupied.contains(edge1.destination().value())) {
										validMoves.add(new DoubleMove(player, new TicketMove(player, UNDERGROUND, edge.destination().value()), new TicketMove(player, SECRET, edge1.destination().value())));
									}
								}
								dummy.addTicket(UNDERGROUND);
							} else if (fromTransport(edge.data()).equals(SECRET) && play.hasTickets(SECRET) && !occoupied.contains(edge.destination().value())) {
								dummy.removeTicket(SECRET);
								Collection<Edge<Integer, Transport>> edgesFromFrom = getGraph().getEdgesFrom(getGraph().getNode(edge.destination().value()));
								for (Edge<Integer, Transport> edge1 : edgesFromFrom) {
									if(!occoupied.contains(edge1.destination().value()))
										validMoves.add(new DoubleMove(player, new TicketMove(player, SECRET, edge.destination().value()), new TicketMove(player, SECRET, edge1.destination().value())));
									if (fromTransport(edge1.data()).equals(TAXI) && play.hasTickets(TAXI) && !occoupied.contains(edge1.destination().value())) {
										validMoves.add(new DoubleMove(player, new TicketMove(player, SECRET, edge.destination().value()), new TicketMove(player, TAXI, edge1.destination().value())));
									} else if (fromTransport(edge1.data()).equals(BUS) && play.hasTickets(BUS) && !occoupied.contains(edge1.destination().value())) {
										validMoves.add(new DoubleMove(player, new TicketMove(player, SECRET, edge.destination().value()), new TicketMove(player, BUS, edge1.destination().value())));
									} else if (fromTransport(edge1.data()).equals(UNDERGROUND) && play.hasTickets(UNDERGROUND)) {
										validMoves.add(new DoubleMove(player, new TicketMove(player, SECRET, edge.destination().value()), new TicketMove(player, UNDERGROUND, edge1.destination().value())));
									} else if (fromTransport(edge1.data()).equals(SECRET) && play.hasTickets(SECRET) && !occoupied.contains(edge1.destination().value())) {
										validMoves.add(new DoubleMove(player, new TicketMove(player, SECRET, edge.destination().value()), new TicketMove(player, SECRET, edge1.destination().value())));
									}
								}
								dummy.addTicket(SECRET);
							}
							if(play.hasTickets(SECRET) && !occoupied.contains(edge.destination().value())){
								dummy.removeTicket(SECRET);
								Collection<Edge<Integer, Transport>> edgesFromFrom = getGraph().getEdgesFrom(getGraph().getNode(edge.destination().value()));
								for (Edge<Integer, Transport> edge1 : edgesFromFrom) {
									if(!occoupied.contains(edge1.destination().value()))
										validMoves.add(new DoubleMove(player, new TicketMove(player, SECRET, edge.destination().value()), new TicketMove(player, SECRET, edge1.destination().value())));
									if (fromTransport(edge1.data()).equals(TAXI) && play.hasTickets(TAXI) && !occoupied.contains(edge1.destination().value())) {
										validMoves.add(new DoubleMove(player, new TicketMove(player, SECRET, edge.destination().value()), new TicketMove(player, TAXI, edge1.destination().value())));
									} else if (fromTransport(edge1.data()).equals(BUS) && play.hasTickets(BUS) && !occoupied.contains(edge1.destination().value())) {
										validMoves.add(new DoubleMove(player, new TicketMove(player, SECRET, edge.destination().value()), new TicketMove(player, BUS, edge1.destination().value())));
									} else if (fromTransport(edge1.data()).equals(UNDERGROUND) && play.hasTickets(UNDERGROUND) && !occoupied.contains(edge1.destination().value())) {
										validMoves.add(new DoubleMove(player, new TicketMove(player, SECRET, edge.destination().value()), new TicketMove(player, UNDERGROUND, edge1.destination().value())));
									} else if (fromTransport(edge1.data()).equals(SECRET) && play.hasTickets(SECRET) && !occoupied.contains(edge1.destination().value())) {
										validMoves.add(new DoubleMove(player, new TicketMove(player, SECRET, edge.destination().value()), new TicketMove(player, SECRET, edge1.destination().value())));
									}
								}
							}
						}
					}
					//TODO: add PassMove
					if (validMoves.isEmpty()) {
						validMoves.add(new PassMove(player));
					}
				}
			}

		return Collections.unmodifiableSet(validMoves);
	}
	@Override
	public void accept(Move move) {
		requireNonNull(move);
		Set<Colour> winners = new HashSet<>();
		//if(!validMove(getCurrentPlayer()).contains(move)) throw new IllegalArgumentException("bad move");
		if (moves.isEmpty()) throw new IllegalArgumentException("empty");
		//if(!validMove(getCurrentPlayer()).contains(move) && getCurrentRound()>0) throw new IllegalArgumentException();
		for(Spectator spectator : spectators){
			spectator.onMoveMade(this, move);
			if(getCurrentPlayer().isMrX()){
				spectator.onRoundStarted(this, getCurrentRound());
			}
			if(isGameOver()){
				if(getCurrentPlayer().isMrX()){
					winners.add(BLACK);
				}else{
					for(ScotlandYardPlayer play:players){
						if(play.colour().isDetective()){
							winners.add(play.colour());
						}
					}
				}
				spectator.onGameOver(this,winners);
			}
		}
	}

	@Override
	public Collection<Spectator> getSpectators() {
		return Collections.unmodifiableList(spectators);
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
		Set<Colour> set = new HashSet<>();
		return Collections.unmodifiableSet(set);
	}

	@Override
	public Optional<Integer> getPlayerLocation(Colour colour) {
		if(colour.equals(BLACK) && roundNo==0){
			lastKnownLocation = 0;
			return Optional.of(lastKnownLocation);
		}
		if(colour.equals(BLACK)){
			if(rounds.get(getCurrentRound()).equals(Boolean.TRUE)){
				lastKnownLocation=players.get(0).location();
				return Optional.of(players.get(0).location());
			}else {
				return Optional.of(lastKnownLocation);
			}
		}
		for(ScotlandYardPlayer player:players){
			if(player.colour().equals(colour)){
				if(player.tickets().isEmpty()){
					return Optional.empty();
				}else{
					return Optional.of(player.location());
				}
			}
		}
		return Optional.empty();
	}

	@Override
	public Optional<Integer> getPlayerTickets(Colour colour, Ticket ticket) {
		for(ScotlandYardPlayer player:players){
			if(player.colour().equals(colour)){
				if(player.tickets().isEmpty()){
					return Optional.empty();
				}else {
					return Optional.of(player.tickets().get(ticket));

				}
			}
		}
		return Optional.empty();
	}

	@Override
	public boolean isGameOver() {
		ScotlandYardPlayer mrx;
		mrx = new ScotlandYardPlayer(players.get(0).player(),players.get(0).colour(),players.get(0).location(),players.get(0).tickets());

		for(ScotlandYardPlayer player: players){
			if(player.isDetective()&&player.location()==mrx.location()){
				return true;
			}
		}

		return false;
		//throw new RuntimeException("Implement me");
	}

	@Override
	public Colour getCurrentPlayer() {
		return players.get(index).colour();
	}

	@Override
	public int getCurrentRound() {
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

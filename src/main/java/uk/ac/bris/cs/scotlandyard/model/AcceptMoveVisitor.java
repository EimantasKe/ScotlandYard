package uk.ac.bris.cs.scotlandyard.model;

import java.util.*;

public class AcceptMoveVisitor implements MoveVisitor  {
    int newLocation;
    boolean moveMade=false;
    boolean doubleMove=false;
    TicketMove firstMove;
    TicketMove secondMove;
    TicketMove singleMove;
    Map<Ticket,Integer> tickets = new HashMap<>();
    List<Ticket> ticketz = new ArrayList<>();
    @Override
    public void visit(PassMove move){
        moveMade=false;
    }
    @Override
    public void visit(TicketMove move){
        //System.out.println("ticket move");
        ticketz.removeAll(ticketz);
        newLocation=move.destination();
        ticketz.add(move.ticket());
        moveMade=true;
        doubleMove=false;
        singleMove=move;
    }
    @Override
    public void visit(DoubleMove move){
        //System.out.println("double move");
        ticketz.removeAll(ticketz);
        moveMade = true;
        doubleMove = true;
        firstMove  = move.firstMove();
        secondMove = move.secondMove();
        ticketz.add(move.firstMove().ticket());
        ticketz.add(move.secondMove().ticket());
        newLocation = move.finalDestination();
    }
}

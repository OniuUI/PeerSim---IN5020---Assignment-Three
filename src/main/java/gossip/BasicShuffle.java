package main.java.gossip;

import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Linkable;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.transport.Transport;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Lucas Provensi
 * <p>
 * Basic Shuffling protocol template
 * <p>
 * The basic shuffling algorithm, introduced by Stavrou et al in the paper:
 * "A Lightweight, Robust P2P System to Handle Flash Crowds", is a simple
 * peer-to-peer communication model. It forms an overlay and keeps it
 * connected by means of an epidemic algorithm. The protocol is extremely
 * simple: each peer knows a small, continuously changing set of other peers,
 * called its neighbors, and occasionally contacts a random one to exchange
 * some of their neighbors.
 * <p>
 * This class is a template with instructions of how to implement the shuffling
 * algorithm in PeerSim.
 * Should make use of the classes Entry and GossipMessage:
 * Entry - Is an entry in the cache, contains a reference to a neighbor node
 * and a reference to the last node this entry was sent to.
 * GossipMessage - The message used by the protocol. It can be a shuffle
 * request, reply or reject message. It contains the originating
 * node and the shuffle list.
 */
public class BasicShuffle implements Linkable, EDProtocol, CDProtocol {

    private static final String PAR_CACHE = "cacheSize";
    private static final String PAR_L = "shuffleLength";
    private static final String PAR_TRANSPORT = "transport";

    private final int time;

    // The bool indicating if node is awaiting response
    private boolean awaitingResponse = false;
    // The bool indicating whether this node removed Q from its cache
    private boolean removedQ = false;

    // The list of neighbors known by this node, or the cache.
    private List<Entry> cache;

    // The maximum size of the cache;
    private final int size;

    // The maximum length of the shuffle exchange;
    private final int l;

    /**
     * Constructor that initializes the relevant simulation parameters and
     * other class variables.
     *
     * @param n simulation parameters
     */
    public BasicShuffle(String n) {
        this.size = Configuration.getInt(n + "." + PAR_CACHE);
        this.l = Configuration.getInt(n + "." + PAR_L);
        this.time = Configuration.getPid(n + "." + PAR_TRANSPORT);

        cache = new ArrayList<Entry>(size);
    }

    /* START YOUR IMPLEMENTATION FROM HERE
     *
     * The simulator engine calls the method nextCycle once every cycle
     * (specified in time units in the simulation script) for all the nodes.
     *
     * You can assume that a node initiates a shuffling operation every cycle.
     *
     * @see peersim.cdsim.CDProtocol#nextCycle(peersim.core.Node, int)
     */
    @Override
    public void nextCycle(Node node, int protocolID) {
        // Implement the shuffling protocol using the following steps (or
        // you can design a similar algorithm):
        // Let's name this node as P

        // 1. If P is waiting for a response from a shuffling operation initiated in a previous cycle, return;
        // 2. If P's cache is empty, return;
        // 3. Select a random neighbor (named Q) from P's cache to initiate the shuffling;
        //	  - You should use the simulator's common random source to produce a random number: CommonState.r.nextInt(cache.size())
        // 4. If P's cache is full, remove Q from the cache;
        // 5. Select a subset of other l - 1 random neighbors from P's cache;
        //	  - l is the length of the shuffle exchange
        //    - Do not add Q to this subset
        // 6. Add P to the subset;
        // 7. Send a shuffle request to Q containing the subset;
        //	  - Keep track of the nodes sent to Q
        //	  - Example code for sending a message:
        //
        // GossipMessage message = new GossipMessage(node, subset);
        // message.setType(MessageType.SHUFFLE_REQUEST);
        // Transport tr = (Transport) node.getProtocol(tid);
        // tr.send(node, Q.getNode(), message, protocolID);
        //
        // 8. From this point on P is waiting for Q's response and will not initiate a new shuffle operation;
        //
        // The response from Q will be handled by the method processEvent.

        //  1.
        if (awaitingResponse)
            return;

        // 2.
        if (cache.isEmpty())
            return;

        // 3.
        // Create a tempCache copy of the cache so that we can remove randomly selected items
        // without accidentally re-selecting them later
        List<Entry> tempCache = new ArrayList<>(cache);
        Entry Q = tempCache.remove(CommonState.r.nextInt(cache.size()));

        // 4.
        if (cache.size() >= size) {
            cache.remove(Q);
            removedQ = true;
        }

        // 5.
        // Create the new subset container
        ArrayList<Entry> neighborSubset = new ArrayList<>();

        // Select min(l - 1, cache.size()) neighbors randomly and add them to the new subset
        for (int i = 0; i < l - 1; i++) {
            if (tempCache.isEmpty())
                break;

            Entry pick = tempCache.remove(CommonState.r.nextInt(tempCache.size()));
            neighborSubset.add(pick);

            pick.setSentTo(Q.getNode());
        }

        // 6.
        Entry P = new Entry(node);
        neighborSubset.add(P);

        // 7.
        // Send the randomly selected subset of neighbors from P to Q
        GossipMessage message = new GossipMessage(node, neighborSubset);
        message.setType(MessageType.SHUFFLE_REQUEST);
        Transport tr = (Transport) node.getProtocol(time);
        tr.send(node, Q.getNode(), message, protocolID);

        // 8.
        awaitingResponse = true;
    }

    /* The simulator engine calls the method processEvent at the specific time unit that an event occurs in the simulation.
     * It is not called periodically as the nextCycle method.
     *
     * You should implement the handling of the messages received by this node in this method.
     *
     * @see peersim.edsim.EDProtocol#processEvent(peersim.core.Node, int, java.lang.Object)
     */
    @Override
    public void processEvent(Node node, int pid, Object event) {
        // Let's name this node as Q;
        // Q receives a message from P;
        //	  - Cast the event object to a message:
        GossipMessage message = (GossipMessage) event;
        Node P = message.getNode();

        switch (message.getType()) {
            // If the message is a shuffle request:
            case SHUFFLE_REQUEST:
                //	  1. If Q is waiting for a response from a shuffling initiated in a previous cycle, send back to P a message rejecting the shuffle request;
                //	  2. Q selects a random subset of size l of its own neighbors;
                //	  3. Q reply P's shuffle request by sending back its own subset;
                //	  4. Q updates its cache to include the neighbors sent by P:
                //		 - No neighbor appears twice in the cache
                //		 - Use empty cache slots to add the new entries
                //		 - If the cache is full, you can replace entries among the ones sent to P with the new ones

                // 1.
                if (awaitingResponse) {
                    GossipMessage rejectionMessage = new GossipMessage(node, null);
                    rejectionMessage.setType(MessageType.SHUFFLE_REJECTED);
                    Transport tr = (Transport) node.getProtocol(time);
                    tr.send(node, P, rejectionMessage, pid);
                    return;
                }

                // 2.
                // Create the subset arraylist and a copy of the cache so that we can remove randomly selected items
                // in order to not re-select them
                ArrayList<Entry> neighborSubset = new ArrayList<>();
                ArrayList<Entry> tempCache = new ArrayList<>(cache);

                // Select min(l, cache.size()) neighbors from the cache
                for (int i = 0; i < l; i++) {
                    if (tempCache.isEmpty())
                        break;

                    // Select the neighbor and add it to the subset
                    Entry pick = tempCache.remove(CommonState.r.nextInt(tempCache.size()));
                    neighborSubset.add(pick);
                }

                for (Entry entry : neighborSubset)
                    entry.setSentTo(P);

                // 3.
                // Send the randomly selected subset of neighbors to P
                GossipMessage reply = new GossipMessage(node, neighborSubset);
                reply.setType(MessageType.SHUFFLE_REPLY);
                Transport tr = (Transport) node.getProtocol(time);
                tr.send(node, P, reply, pid);

                // 4. Q updates its cache to include the neighbors sent by P:
                //		 - No neighbor appears twice in the cache
                //		 - Use empty cache slots to add the new entries
                //		 - If the cache is full, you can replace entries among the ones sent to P with the new ones

                List<Entry> shuffleList = message.getShuffleList();

                // Keep track of which nodes sent to P we haven't replaced in our own cache
                ArrayList<Entry> sentNeighbors = new ArrayList<>(neighborSubset);

                // Loop through each node received from P
                for (Entry entry : shuffleList) {
                    // If the node is already in our cache, we skip the entry
                    if (cache.contains(entry))
                        continue;

                    // If the cache size is full
                    if (cache.size() >= size) {
                        // If we don't have any nodes that we sent to P to replace in our own cache, we skip the entry
                        if (sentNeighbors.isEmpty())
                            continue;

                        // Find the index of a node that we sent to P so that we can replace it in our own cache,
                        // then replace it with the entry from P
                        int replaceIndex = cache.indexOf(sentNeighbors.remove(0));
                        cache.set(replaceIndex, entry);
                    } else {
                        cache.add(entry);
                    }
                }
                break;

            // If the message is a shuffle reply:
            case SHUFFLE_REPLY:
                //	  1. In this case Q initiated a shuffle with P and is receiving a response containing a subset of P's neighbors
                //	  2. Q updates its cache to include the neighbors sent by P:
                //		 - No neighbor appears twice in the cache
                //		 - Use empty cache slots to add new entries
                //		 - If the cache is full, you can replace entries among the ones originally sent to P with the new ones
                //	  3. Q is no longer waiting for a shuffle reply;

                // 1.
                shuffleList = message.getShuffleList();

                // Find which indices we can replace in the cache (because they contain entries sent to P)
                ArrayList<Integer> replaceIndices = new ArrayList<>();
                for (int i = 0; i < cache.size(); i++) {
                    if (cache.get(i).getSentTo() == P)
                        replaceIndices.add(i);
                }

                // Loop through each node received from P
                for (Entry entry : shuffleList) {
                    // If the node is already in our cache, we skip the entry
                    if (cache.contains(entry))
                        continue;

                    if (cache.size() >= size) {
                        if (replaceIndices.size() > 0) {
                            int replaceIndex = replaceIndices.remove(0);
                            cache.set(replaceIndex, entry);
                        }
                    }
                    else {
                        cache.add(entry);
                    }
                }

                // 3.
                awaitingResponse = false;

                removedQ = false;
                break;

            // If the message is a shuffle rejection:
            case SHUFFLE_REJECTED:
                //	  1. If P was originally removed from Q's cache, add it again to the cache.
                //	  2. Q is no longer waiting for a shuffle reply;
                if (removedQ)
                    cache.add(new Entry(P));

                removedQ = false;
                awaitingResponse = false;
                break;

            default:
                break;
        }

    }

    /* The following methods are used only by the simulator and don't need to be changed */

    @Override
    public int degree() {
        return cache.size();
    }

    @Override
    public Node getNeighbor(int i) {
        return cache.get(i).getNode();
    }

    @Override
    public boolean addNeighbor(Node neighbour) {
        if (contains(neighbour))
            return false;

        if (cache.size() >= size)
            return false;

        Entry entry = new Entry(neighbour);
        cache.add(entry);

        return true;
    }

    @Override
    public boolean contains(Node neighbor) {
        return cache.contains(new Entry(neighbor));
    }

    public Object clone() {
        BasicShuffle gossip = null;
        try {
            gossip = (BasicShuffle) super.clone();
        } catch (CloneNotSupportedException e) {

        }
        gossip.cache = new ArrayList<Entry>();

        return gossip;
    }

    @Override
    public void onKill() {
        // TODO Auto-generated method stub
    }

    @Override
    public void pack() {
        // TODO Auto-generated method stub
    }
}

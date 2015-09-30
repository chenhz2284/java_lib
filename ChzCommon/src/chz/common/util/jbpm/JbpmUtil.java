package chz.common.util.jbpm;

import java.util.List;

import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Transition;

public class JbpmUtil {
	
	/*
	 * 
	 */
	public static Transition findTransition(Node fromNode, Node toNode){
		Transition rtTransition = null;
		List<Transition> transitionList = fromNode.getLeavingTransitions();
		for( int i=0; i<transitionList.size(); i++ ){
			Transition transition = transitionList.get(i);
			if( transition.getTo()==toNode ){
				rtTransition = transition;
				break;
			}
		}
		return rtTransition;
	}

	/*
	 * 
	 */
	public static Transition createTransition(Node fromNode, Node toNode){
		Transition transition = new Transition();
		transition.setFrom(fromNode);
		transition.setTo(toNode);
		return transition;
	}
	
	/*
	 * 
	 */
	public static Transition findOrCreateTransition(Node fromNode, Node toNode){
		Transition rtTransition = findTransition(fromNode, toNode);
		if( rtTransition==null ){
			rtTransition = createTransition(fromNode, toNode);
		}
		return rtTransition;
	}
}

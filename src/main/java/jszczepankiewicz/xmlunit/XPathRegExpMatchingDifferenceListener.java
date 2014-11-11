package jszczepankiewicz.xmlunit;

import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceListener;
import org.w3c.dom.Node;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Difference listener that matches every difference found with list of known xpath locations. If location is not
 * found test fails. If location is found on provided list then child text is matched against regexp
 * provided as corresponding to given location. If not matched whole test fails.
 */
public class XPathRegExpMatchingDifferenceListener implements DifferenceListener {

    private Map<Pattern, Pattern> compiledChildrenForGivenLocations;

    public XPathRegExpMatchingDifferenceListener(Map<String, String> diffs){
        this.compiledChildrenForGivenLocations = compileIgnoredPatterns(diffs);
    }

    private Map<Pattern, Pattern> compileIgnoredPatterns(final Map<String, String> locationsWithMatchers) {

        final Map<Pattern, Pattern> compiledLocationsWithMatchers = new HashMap<Pattern, Pattern>();

        for(String location:locationsWithMatchers.keySet()){
            compiledLocationsWithMatchers.put(Pattern.compile(location),Pattern.compile(locationsWithMatchers.get(location)));
        }

        return compiledLocationsWithMatchers;
    }

    @Override
    public int differenceFound(Difference difference) {

        final String differentNodeLocation = difference.getTestNodeDetail().getXpathLocation();
        final String differentValue = difference.getTestNodeDetail().getValue();

        for(Pattern pattern:compiledChildrenForGivenLocations.keySet()){
            if(pattern.matcher(differentNodeLocation).find()){
                if(compiledChildrenForGivenLocations.get(pattern).matcher(differentValue).find()){
                    return RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
                }
                else{
                    return RETURN_ACCEPT_DIFFERENCE;
                }
            }
        }

        return RETURN_ACCEPT_DIFFERENCE;
    }

    /**
     * Unimportant in current context.
     *
     * @param node
     * @param node1
     */
    @Override
    public void skippedComparison(Node node, Node node1) {

    }
}

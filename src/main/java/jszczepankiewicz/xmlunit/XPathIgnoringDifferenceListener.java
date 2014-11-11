package jszczepankiewicz.xmlunit;

import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceListener;
import org.w3c.dom.Node;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Difference listener that ignores every difference when the xpath location of it is matched using regexp
 * to provided list.
 */
public class XPathIgnoringDifferenceListener implements DifferenceListener {

    private final Set<Pattern> ignoredPatterns;

    /**
     * Constructs difference listener with list of xpath location of nodes that will be ignored for difference searching.
     * @param ignoredPatterns
     */
    public XPathIgnoringDifferenceListener(String... ignoredPatterns) {
        this.ignoredPatterns = compileIgnoredPatterns(new HashSet<>(Arrays.asList(ignoredPatterns)));
    }

    private Set<Pattern> compileIgnoredPatterns(final Set<String> ignoredPatterns) {

        final Set<Pattern> compiledRegexList = new HashSet<Pattern>();

        for(String location:ignoredPatterns){
            compiledRegexList.add(Pattern.compile(location));
        }
        return compiledRegexList;
    }

    @Override
    public int differenceFound(Difference difference) {

        final String differentNodeLocation = difference.getTestNodeDetail().getXpathLocation();

        for(Pattern pattern:ignoredPatterns){
            if(pattern.matcher(differentNodeLocation).find()){
                return RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
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
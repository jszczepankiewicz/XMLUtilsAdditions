package jszczepankiewicz.xmlunit;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.DifferenceListener;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


public class XPathIgnoringDifferenceListenerTest {

    private DifferenceListener differenceListener;

    @BeforeClass
    public static void initAll() {
        XMLUnit.setIgnoreWhitespace(true);
    }

    @Before
    public void init() {
        differenceListener = new XPathIgnoringDifferenceListener(
                "\\Q/location[1]/street-address[1]/text()[1]\\E",
                "\\Q/location[1]/postcode[1]/text()[1]\\E");
    }

    @Test
    public void shouldPassIdenticalXML() throws IOException, SAXException {

        //  given
        String expectedXML = "<location><street-address>22 any street</street-address><postcode>XY00 99Z</postcode></location>";
        String producedXML = "<location><street-address>22 any street</street-address><postcode>XY00 99Z</postcode>\n</location>";
        Diff myDiff = new Diff(expectedXML, producedXML);

        //  when
        myDiff.overrideDifferenceListener(differenceListener);

        //  then
        assertThat(myDiff.identical(), is(true));
        assertThat(myDiff.similar(), is(true));
    }

    @Test
    public void shouldPassOnOneOfXPathIgnoredNodeFound() throws IOException, SAXException {

        //  given
        String expectedXML = "<location><postcode>XY00 99Z</postcode></location>";
        String producedXML = "<location><postcode>mayBeDifferent</postcode>\n</location>";
        Diff myDiff = new Diff(expectedXML, producedXML);

        //  when
        myDiff.overrideDifferenceListener(differenceListener);

        //  then
        assertThat(myDiff.similar(), is(true));
        assertThat(myDiff.identical(), is(true));

    }

    @Test
    public void shouldPassOnBothXPathIgnoredNodeFound() throws IOException, SAXException {

        //  given
        String expectedXML = "<location></location>";
        String producedXML = "<location>\n</location>";
        Diff myDiff = new Diff(expectedXML, producedXML);

        //  when
        myDiff.overrideDifferenceListener(differenceListener);

        //  then
        assertThat(myDiff.similar(), is(true));
        assertThat(myDiff.identical(), is(true));
    }

    @Test
    public void shouldNotPassIfNoneOfIgnoredDiffFound() throws IOException, SAXException {

        //  given
        String expectedXML = "<location><street-addressx>22 any street</street-addressx><postcoder>XY00 99Z</postcoder></location>";
        String producedXML = "<location><street-address>22 any street</street-address><postcode>XY00 99Z</postcode>\n</location>";
        Diff myDiff = new Diff(expectedXML, producedXML);

        //  when
        myDiff.overrideDifferenceListener(differenceListener);

        //  then
        assertThat(myDiff.similar(), is(false));
        assertThat(myDiff.identical(), is(false));
    }

}
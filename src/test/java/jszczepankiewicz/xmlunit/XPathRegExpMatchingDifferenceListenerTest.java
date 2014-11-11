package jszczepankiewicz.xmlunit;


import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.DifferenceListener;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class XPathRegExpMatchingDifferenceListenerTest {

    private DifferenceListener differenceListener;

    @BeforeClass
    public static void initAll() {
        XMLUnit.setIgnoreWhitespace(true);
    }

    @Before
    public void init() {

        Map<String, String> diffs = new HashMap<>();
        diffs.put("\\Q/location[1]/street-address[1]/text()[1]\\E","[tT]rue|[yY]es");
        diffs.put("\\Q/location[1]/postcode[1]/text()[1]\\E","[a-zA-Z]{3}");
        differenceListener = new XPathRegExpMatchingDifferenceListener(diffs);
    }

    @Test
    public void shouldPassIdenticalXML() throws IOException, SAXException {

        //  given
        String expectedXML = "<location><street-address>true or yes allowed here</street-address><postcode>Three letters allowed here</postcode></location>";
        String producedXML = "<location><street-address>true or yes allowed here</street-address><postcode>Three letters allowed here</postcode>\n</location>";
        Diff myDiff = new Diff(expectedXML, producedXML);

        //  when
        myDiff.overrideDifferenceListener(differenceListener);

        //  then
        assertThat(myDiff.identical(), is(true));
        assertThat(myDiff.similar(), is(true));
    }

    @Test
    public void shouldPassOnBothLocationsFoundAndContentMatched() throws IOException, SAXException {

        //  given
        String expectedXML = "<location><street-address>true or yes allowed here</street-address><postcode>Three letters allowed here</postcode></location>";
        String producedXML = "<location><street-address>Yes</street-address><postcode>aBc</postcode>\n</location>";
        Diff myDiff = new Diff(expectedXML, producedXML);

        //  when
        myDiff.overrideDifferenceListener(differenceListener);

        //  then
        assertThat(myDiff.identical(), is(true));
        assertThat(myDiff.similar(), is(true));
    }

    @Test
    public void shouldPassOnOneLocationFoundAndContentMatched() throws IOException, SAXException {

        //  given
        String expectedXML = "<location><street-address>true or yes allowed here</street-address><postcode>Three letters allowed here</postcode></location>";
        String producedXML = "<location><street-address>true or yes allowed here</street-address><postcode>aBc</postcode>\n</location>";
        Diff myDiff = new Diff(expectedXML, producedXML);

        //  when
        myDiff.overrideDifferenceListener(differenceListener);

        //  then
        assertThat(myDiff.identical(), is(true));
        assertThat(myDiff.similar(), is(true));
    }

    @Test
    public void shouldFailIfOneLocationNotFound() throws IOException, SAXException {

        //  given
        String expectedXML = "<location><street-address>true or yes allowed here</street-address><zip>Three letters allowed here</zip></location>";
        String producedXML = "<location><street-address>true</street-address><zip>aBc</zip>\n</location>";
        Diff myDiff = new Diff(expectedXML, producedXML);

        //  when
        myDiff.overrideDifferenceListener(differenceListener);

        //  then
        assertThat(myDiff.identical(), is(false));
        assertThat(myDiff.similar(), is(false));
    }

    @Test
    public void shouldFailIfTwoLocationsNotFound() throws IOException, SAXException{

        //  given
        String expectedXML = "<location><latitude/><zipcode>X</zipcode></location>";
        String producedXML = "<location><latitude>234</latitude><zipcode>Y</zipcode>\n</location>";
        Diff myDiff = new Diff(expectedXML, producedXML);

        //  when
        myDiff.overrideDifferenceListener(differenceListener);

        //  then
        assertThat(myDiff.identical(), is(false));
        assertThat(myDiff.similar(), is(false));
    }

    @Test
    public void shouldFailIfLocationFoundButNotMatched() throws IOException, SAXException {

        //  given
        String expectedXML = "<location><street-address>true or yes allowed here</street-address></location>";
        String producedXML = "<location><street-address>semiboolean</street-address>\n</location>";
        Diff myDiff = new Diff(expectedXML, producedXML);

        //  when
        myDiff.overrideDifferenceListener(differenceListener);

        //  then
        assertThat(myDiff.identical(), is(false));
        assertThat(myDiff.similar(), is(false));
    }

    @Test
    public void shouldFailIfBothLocationsFoundBotNotMatched() throws IOException, SAXException{

        //  given
        String expectedXML = "<location><street-address>true or yes allowed here</street-address><postcode>Three letters allowed here</postcode></location>";
        String producedXML = "<location><street-address>Santa Barbara</street-address><postcode>12345</postcode>\n</location>";
        Diff myDiff = new Diff(expectedXML, producedXML);

        //  when
        myDiff.overrideDifferenceListener(differenceListener);

        //  then
        assertThat(myDiff.identical(), is(false));
        assertThat(myDiff.similar(), is(false));
    }

}
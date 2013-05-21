package org.biopax.paxtools.pattern.miner;

import org.biopax.paxtools.model.BioPAXElement;
import org.biopax.paxtools.model.level3.Control;
import org.biopax.paxtools.model.level3.ProteinReference;
import org.biopax.paxtools.pattern.Match;
import org.biopax.paxtools.pattern.Pattern;
import org.biopax.paxtools.pattern.PatternBox;
import org.biopax.paxtools.pattern.constraint.Type;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Miner for the transcriptional regulation pattern.
 * @author Ozgun Babur
 */
public class TranscriptionalRegulationMiner extends MinerAdapter implements SIFMiner
{
	/**
	 * Constructor that sets name and description.
	 */
	public TranscriptionalRegulationMiner()
	{
		super("Transcriptional-regulation", "This pattern finds relations where first protein " +
			"is controlling transcriptional activity of the second protein. The output is either " +
			"\"A transactivate B\" or \"A transinhibit B\". This pattern requires that " +
			"transcription to be modeled with a TemplateReaction.");
	}

	/**
	 * Constructs the pattern.
	 * @return pattern
	 */
	@Override
	public Pattern constructPattern()
	{
		Pattern p = PatternBox.transcriptionWithTemplateReac();
		p.addConstraint(new Type(ProteinReference.class), "product ER");
		return p;
	}

	/**
	 * Writes the result as "A transactivate B" or "A transinhibit B", where A and B are gene
	 * symbols, and whitespace is tab.
	 * @param matches pattern search result
	 * @param out output stream
	 */
	@Override
	public void writeResult(Map<BioPAXElement, List<Match>> matches, OutputStream out)
		throws IOException
	{
		writeResultAsSIF(matches, out, true, getSourceLabel(), getTargetLabel());
	}

	@Override
	public String getSourceLabel()
	{
		return "TF PR";
	}

	@Override
	public String getTargetLabel()
	{
		return "product ER";
	}

	/**
	 * The relation can be either transactivate for transactivation, or transinhibit for
	 * transinhibition.
	 * @param m the match
	 * @return type
	 */
	@Override
	public String getRelationType(Match m)
	{
		Control con = (Control) m.get("Control", getPattern());
		return con.getControlType() != null && con.getControlType().toString().startsWith("I") ?
			"transinhibit" : "transactivate";
	}

	@Override
	public boolean isDirected()
	{
		return true;
	}

	/**
	 * Sets header of the output.
	 * @return header
	 */
	@Override
	public String getHeader()
	{
		return "Upstream\ttype\tDownstream";
	}

	@Override
	public String[] getPubmedHarvestableLabels()
	{
		return new String[]{"Control", "TempReac"};
	}
}

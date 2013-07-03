package edu.isi.bmkeg.skm.core.uima.ae.ml.features;

import java.util.HashMap;
import java.util.Map;

public class AutoSlogFeature
{

	/**
	 * CF: 
		Name: <POS_NUM_Modifiers>NP:POS__&&0
		Negation: false
		Anchor: NP1(&&0)
		Act_Fcns: np_contains_POStagDefined_p(NP1()  NUMBER )
		          has_head_p(NP1(&&0) )
		Slot: np
		Stats: frequency = 1355
        relativeFreq =  52
        cond_prob = 0.0383764
        rlog_score = 0.218762
	 */
	public static String CF="CF:";
	public static String Name="Name:";
	public static String Negation="Negation:";
	public static String Anchor="Anchor:";
	public static String Act_Fncs="Act_Fcns:";
	public static String Slot="Slot:";
	public static String Stats="Stats: frequency =";
	public static String relativeFreq="relativeFreq";
	public static String cond_prob="cond_prob";
	public static String rlog_score="rlog_score";
	public static String frequency="frequency";
	private String name;
	private Boolean isNegation;
	private String anchor;
	private String act_fcns;
	private String slot;
	private Map<String,Double> stats;

	public AutoSlogFeature()
	{
		stats = new HashMap<String, Double>();
	}

	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public Boolean getIsNegation()
	{
		return isNegation;
	}
	public void setIsNegation(Boolean isNegation)
	{
		this.isNegation = isNegation;
	}
	public String getAnchor()
	{
		return anchor;
	}
	public void setAnchor(String anchor)
	{
		this.anchor = anchor;
	}
	public String getAct_fcns()
	{
		return act_fcns;
	}
	public void setAct_fcns(String actFcns)
	{
		act_fcns = actFcns;
	}
	public String getSlot()
	{
		return slot;
	}
	public void setSlot(String slot)
	{
		this.slot = slot;
	}
	public Map<String, Double> getStats()
	{
		return stats;
	}
	public void addStat(String key, String value)
	{
		Double val = new Double(value);
		this.stats.put(key, val);
	}

	@Override
	public String toString()
	{
		// TODO Auto-generated method stub
		return this.name+"--["+this.isNegation+"]--["+this.anchor+"]--["+this.act_fcns+"]--["+this.slot+"]";
	}
}


import java.io.*;

/// Affiliaiton Graph Model (AGM) graph generator.
public class TAGM
{

  /////////////////////////////////////////////////
  // AGM graph generation.

  ///Connect members of a given community by Erdos-Renyi
  public static void RndConnectInsideCommunity(TPt<TUNGraph> Graph, TVec<TInt> CmtyV, double Prob, TRnd Rnd)
  {
	int CNodes = CmtyV.Len();
	int CEdges;
	if (CNodes < 20)
	{
	  CEdges = (int) Rnd.GetBinomialDev(Prob, CNodes * (CNodes - 1) / 2);
	}
	else
	{
	  CEdges = (int)(Prob * CNodes * (CNodes - 1) / 2);
	}
	THashSet<TPair<TInt, TInt>> NewEdgeSet = new THashSet<TPair<TInt, TInt>>(CEdges);
	for (int edge = 0; edge < CEdges;)
	{
	  int SrcNId = CmtyV.get(Rnd.GetUniDevInt(CNodes));
	  int DstNId = CmtyV.get(Rnd.GetUniDevInt(CNodes));
	  if (SrcNId > DstNId)
	  {
		  tangible.RefObject<TRec> tempRef_SrcNId = new tangible.RefObject<TRec>(SrcNId);
		  tangible.RefObject<TRec> tempRef_DstNId = new tangible.RefObject<TRec>(DstNId);
		  GlobalMembers.Swap(tempRef_SrcNId, tempRef_DstNId);
		  DstNId = tempRef_DstNId.argValue;
		  SrcNId = tempRef_SrcNId.argValue;
	  }
	  if (SrcNId != DstNId && !NewEdgeSet.IsKey(TIntPr(SrcNId, DstNId)))
	  { // is new edge
		NewEdgeSet.AddKey(TIntPr(SrcNId, DstNId));
		Graph.dereference().AddEdge(SrcNId, DstNId);
		edge++;
	  }
	}
  }

//  static TPt<TUNGraph> GenAGM(TVec<TInt> NIdV, THash<TInt,TVec<TInt>> CmtyVH, TStr AGMNm, double PiCoef, double ProbBase, TRnd Rnd);
  public static TPt<TUNGraph> GenAGM(TVec<TVec<TInt>> CmtyVV, double DensityCoef, double ScaleCoef)
  {
	  return GenAGM(CmtyVV, DensityCoef, ScaleCoef, TInt.Rnd);
  }
//ORIGINAL LINE: static TPt<TUNGraph> GenAGM(TVec<TVec<TInt>>& CmtyVV, const double& DensityCoef, const double& ScaleCoef, TRnd& Rnd =TInt::Rnd)
  public static TPt<TUNGraph> GenAGM(TVec<TVec<TInt>> CmtyVV, double DensityCoef, double ScaleCoef, TRnd Rnd)
  {
	TVec<TFlt> CProbV = new TVec<TFlt>();
	double Prob;
	for (int i = 0; i < CmtyVV.Len(); i++)
	{
	  Prob = ScaleCoef * Math.pow((double)(CmtyVV.get(i).Len()), - DensityCoef);
	  if (Prob > 1.0)
	  {
		  Prob = 1;
	  }
	  CProbV.Add(Prob);
	}
	return new TPt<TUNGraph>(GenAGM.functorMethod(CmtyVV, CProbV, Rnd));
  }

  public static TPt<TUNGraph> GenAGM(TVec<TVec<TInt>> CmtyVV, double DensityCoef, int TargetEdges, TRnd Rnd)
  {
	TPt<TUNGraph> TryG = GenAGM.functorMethod(CmtyVV, DensityCoef, 1.0, Rnd);
	final double ScaleCoef = (double) TargetEdges / (double) TryG.dereference().GetEdges();
	return new TPt<TUNGraph>(GenAGM.functorMethod(CmtyVV, DensityCoef, ScaleCoef, Rnd));
  }


  ///Generate graph using the AGM model. CProbV = vector of Pc
  public static TPt<TUNGraph> GenAGM(TVec<TVec<TInt>> CmtyVV, TVec<TFlt> CProbV, TRnd Rnd)
  {
	  return GenAGM(CmtyVV, CProbV, Rnd, -1.0);
  }
CmtyVV, const TVec<TFlt>& CProbV, TRnd& Rnd, const double PNoCom = -1.0)
  public static TPt<TUNGraph> GenAGM(TVec<TVec<TInt>> CmtyVV, TVec<TFlt> CProbV, TRnd Rnd, double PNoCom)
  {
	TPt<TUNGraph> G = TUNGraph.New.functorMethod(100 * CmtyVV.Len(), -1);
	System.out.print("AGM begins\n");
	for (int i = 0; i < CmtyVV.Len(); i++)
	{
	  TVec<TInt> CmtyV = CmtyVV.get(i);
	  for (int u = 0; u < CmtyV.Len(); u++)
	  {
		if (G.dereference().IsNode(CmtyV.get(u)))
		{
			continue;
		}
		G.dereference().AddNode(CmtyV.get(u));
	  }
	  double Prob = CProbV.get(i);
	  RndConnectInsideCommunity(G.functorMethod, CmtyV, Prob, Rnd);
	}
	if (PNoCom > 0.0)
	{ //if we want to connect nodes that do not share any community
	  THashSet<TInt> NIDS = new THashSet<TInt>();
	  for (int c = 0; c < CmtyVV.Len(); c++)
	  {
		for (int u = 0; u < CmtyVV.get(c).Len(); u++)
		{
		  NIDS.AddKey(CmtyVV.get(c)[u]);
		}
	  }
	  TVec<TInt> NIDV = new TVec<TInt>();
	  NIDS.GetKeyV(NIDV);
	  RndConnectInsideCommunity(G.functorMethod, NIDV, PNoCom, Rnd);
	}
	System.out.printf("AGM completed (%d nodes %d edges)\n",G.dereference().GetNodes(),G.dereference().GetEdges());
	G.dereference().Defrag();
//ORIGINAL LINE: return G;
	return new TPt<TUNGraph>(G.functorMethod);
  }
}

/// Affiliaiton Graph Model (AGM) graph generator.
public class TAGMUtil
{

  /// AGMUtil:: Utilities for AGM

  ///Generate sequence from Power law
  public static void GenPLSeq(TVec<TInt> SzSeq, int SeqLen, double Alpha, TRnd Rnd, int Min, int Max)
  {
	SzSeq.Gen(SeqLen, 0);
	while (SzSeq.Len() < SeqLen)
	{
	  int Sz = (int) TMath.Round(Rnd.GetPowerDev(Alpha));
	  if (Sz >= Min && Sz <= Max)
	  {
		SzSeq.Add(Sz);
	  }
	}
  }


  ///Generate bipartite community affiliation from given power law coefficients for membership distribution and community size distribution.
  public static void ConnectCmtyVV(TVec<TVec<TInt>> CmtyVV, TVec<TPair<TInt, TInt>> CIDSzPrV, TVec<TPair<TInt, TInt>> NIDMemPrV, TRnd Rnd)
  {
	final int Nodes = NIDMemPrV.Len();
	final int Coms = CIDSzPrV.Len();
	TVec<TInt> NDegV = new TVec<TInt>();
	TVec<TInt> CDegV = new TVec<TInt>();
	THashSet<TPair<TInt, TInt>> CNIDSet = new THashSet<TPair<TInt, TInt>>();
	THashSet<TInt> HitNodes = new THashSet<TInt>(Nodes);
	THash<TInt,TVec<TInt>> CmtyVH = new THash<TInt,TVec<TInt>>();
	for (int i = 0;i < CIDSzPrV.Len(); i++)
	{
	  for (int j = 0; j < CIDSzPrV.get(i).Val2; j++)
	  {
		CDegV.Add(CIDSzPrV.get(i).Val1);
	  }
	}
	for (int i = 0; i < NIDMemPrV.Len(); i++)
	{
	  for (int j = 0; j < NIDMemPrV.get(i).Val2; j++)
	  {
		NDegV.Add(NIDMemPrV.get(i).Val1);
	  }
	}
	while (CDegV.Len() < (int)(1.2 * Nodes))
	{
	  CDegV.Add(CIDSzPrV.get(Rnd.GetUniDevInt(Coms)).Val1);
	}
	while (NDegV.Len() < CDegV.Len())
	{
	  NDegV.Add(NIDMemPrV.get(Rnd.GetUniDevInt(Nodes)).Val1);
	}
	System.out.printf("Total Mem: %d, Total Sz: %d\n",NDegV.Len(), CDegV.Len());
	int c = 0;
	while (c++ < 15 && CDegV.Len() > 1)
	{
	  for (int i = 0; i < CDegV.Len(); i++)
	  {
		int u = Rnd.GetUniDevInt(CDegV.Len());
		int v = Rnd.GetUniDevInt(NDegV.Len());
		if (CNIDSet.IsKey(TIntPr(CDegV.get(u), NDegV.get(v))))
		{
			continue;
		}
		CNIDSet.AddKey(TIntPr(CDegV.get(u), NDegV.get(v)));
		HitNodes.AddKey(NDegV.get(v));
		if (u == CDegV.Len() - 1)
		{

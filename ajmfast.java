public class TAGMFastUtil
{
  //static double GetConductance(const PUNGraph& Graph, const TIntSet& CmtyS, const int Edges);
  //static double GetConductance(const PNGraph& Graph, const TIntSet& CmtyS, const int Edges);
//C++ TO JAVA CONVERTER TODO TASK: The original C++ template specifier was replaced with a Java generic specifier, which may not produce the same behavior:
//ORIGINAL LINE: template<class PGraph>
public static <PGraph> double GetConductance(PGraph Graph, THashSet<TInt> CmtyS, int Edges)
{
  final boolean GraphType = ((TGraphFlag.gfDirected) == TGraphFlag.gfDirected ? TSnap.IsDirected<typename PGraph.TObj.TNet>.Val : (TGraphFlag.gfDirected) == TGraphFlag.gfMultiGraph ? TSnap.IsMultiGraph<typename PGraph.TObj.TNet>.Val : (TGraphFlag.gfDirected) == TGraphFlag.gfNodeDat ? TSnap.IsNodeDat<typename PGraph.TObj.TNet>.Val : (TGraphFlag.gfDirected) == TGraphFlag.gfEdgeDat ? TSnap.IsEdgeDat<typename PGraph.TObj.TNet>.Val : (TGraphFlag.gfDirected) == TGraphFlag.gfSources ? TSnap.IsSources<typename PGraph.TObj.TNet>.Val : (TGraphFlag.gfDirected) == TGraphFlag.gfBipart ? TSnap.IsBipart<typename PGraph.TObj.TNet>.Val : 0);
  int Edges2;
  if (GraphType)
  {
	  Edges2 = Edges >= 0 ? Edges : Graph.GetEdges();
  }
  else
  {
	  Edges2 = Edges >= 0 ? 2 * Edges : Graph.GetEdges();
  }
  int Vol = 0;
  int Cut = 0;
  double Phi = 0.0;
  for (int i = 0; i < CmtyS.Len(); i++)
  {
	if (!Graph.IsNode(CmtyS.get(i)))
	{
		continue;
	}
	PGraph.TObj.TNodeI NI = Graph.GetNI(CmtyS.get(i));
	for (int e = 0; e < NI.GetOutDeg(); e++)
	{
	  if (!CmtyS.IsKey(NI.GetOutNId(e)))
	  {
		  Cut += 1;
	  }
	}
	Vol += NI.GetOutDeg();
  }
  // get conductance
  if (Vol != Edges2)
  {
	if (2 * Vol > Edges2)
	{
		Phi = Cut / (double)(Edges2 - Vol);
	}
	else if (Vol == 0)
	{
		Phi = 0.0;
	}
	else
	{
		Phi = Cut / (double)Vol;
	}
  }
  else
  {
	if (Vol == Edges2)
	{
		Phi = 1.0;
	}
  }
  return Phi;
}


//C++ TO JAVA CONVERTER TODO TASK: The original C++ template specifier was replaced with a Java generic specifier, which may not produce the same behavior:
//ORIGINAL LINE: template<class PGraph>
  public static <PGraph> void GenHoldOutPairs(PGraph G, TVec<THashSet<TInt>> HoldOutSet, double HOFrac, TRnd Rnd)
  {
	TVec<TPair<TInt, TInt>> EdgeV = new TVec<TPair<TInt, TInt>>(G.GetEdges(), 0);
	for (typename PGraph.TObj.TEdgeI EI = G.BegEI(); EI < G.EndEI(); EI++)
	{
	  EdgeV.Add(TIntPr(EI.GetSrcNId(), EI.GetDstNId()));
	}
	EdgeV.Shuffle(Rnd);

	final boolean GraphType = ((TGraphFlag.gfDirected) == TGraphFlag.gfDirected ? TSnap.IsDirected<typename PGraph.TObj.TNet>.Val : (TGraphFlag.gfDirected) == TGraphFlag.gfMultiGraph ? TSnap.IsMultiGraph<typename PGraph.TObj.TNet>.Val : (TGraphFlag.gfDirected) == TGraphFlag.gfNodeDat ? TSnap.IsNodeDat<typename PGraph.TObj.TNet>.Val : (TGraphFlag.gfDirected) == TGraphFlag.gfEdgeDat ? TSnap.IsEdgeDat<typename PGraph.TObj.TNet>.Val : (TGraphFlag.gfDirected) == TGraphFlag.gfSources ? TSnap.IsSources<typename PGraph.TObj.TNet>.Val : (TGraphFlag.gfDirected) == TGraphFlag.gfBipart ? TSnap.IsBipart<typename PGraph.TObj.TNet>.Val : 0);
	HoldOutSet.Gen(G.GetNodes());
	int HOTotal = (int)HOFrac * G.GetNodes() * (G.GetNodes() - 1) / 2.0;
	if (GraphType)
	{
		HOTotal *= 2;
	}
	int HOCnt = 0;
	int HOEdges = (int) TMath.Round(HOFrac * G.GetEdges());
	System.out.printf("holding out %d edges...\n", HOEdges);
	for (int he = 0; he < (int) HOEdges; he++)
	{
	  HoldOutSet.get(EdgeV.get(he).Val1).AddKey(EdgeV.get(he).Val2);
	  if (!GraphType)
	  {
		  HoldOutSet.get(EdgeV.get(he).Val2).AddKey(EdgeV.get(he).Val1);
	  }
	  HOCnt++;
	}
	System.out.printf("%d Edges hold out\n", HOCnt);
	while (HOCnt++ < HOTotal)
	{
	  int SrcNID = Rnd.GetUniDevInt(G.GetNodes());
	  int DstNID = Rnd.GetUniDevInt(G.GetNodes());
	  if (SrcNID == DstNID)
	  {
		  continue;
	  }
	  HoldOutSet.get(SrcNID).AddKey(DstNID);
	  if (!GraphType)
	  {
		  HoldOutSet.get(DstNID).AddKey(SrcNID);
	  }
	}
  }
//C++ TO JAVA CONVERTER TODO TASK: The original C++ template specifier was replaced with a Java generic specifier, which may not produce the same behavior:
//ORIGINAL LINE: template<class PGraph>
  public static <PGraph> void GetNbhCom(PGraph Graph, int NID, THashSet<TInt> NBCmtyS)
  {
	PGraph.TObj.TNodeI NI = Graph.GetNI(NID);
	NBCmtyS.Gen(NI.GetDeg());
	NBCmtyS.AddKey(NID);
	for (int e = 0; e < NI.GetDeg(); e++)
	{
	  NBCmtyS.AddKey(NI.GetNbrNId(e));
	}
  }
//C++ TO JAVA CONVERTER TODO TASK: The original C++ template specifier was replaced with a Java generic specifier, which may not produce the same behavior:
//ORIGINAL LINE: template<class PGraph>
  public static <PGraph> void GetNIdPhiV(PGraph G, TVec<TPair<TFlt, TInt>> NIdPhiV)
  {
	NIdPhiV.Gen(G.GetNodes(), 0);
	final int Edges = G.GetEdges();
	TExeTm RunTm = new TExeTm();
	//compute conductance of neighborhood community
	for (typename PGraph.TObj.TNodeI NI = G.BegNI(); NI < G.EndNI(); NI++)
	{
	  THashSet<TInt> NBCmty = new THashSet<TInt>(NI.GetDeg() + 1);
	  double Phi;
	  if (NI.GetDeg() < 5)
	  { //do not include nodes with too few degree
		Phi = 1.0;
	  }
	  else
	  {
		TAGMFastUtil.<PGraph>GetNbhCom(G, NI.GetId(), NBCmty);
		//if (NBCmty.Len() != NI.GetDeg() + 1) { printf("NbCom:%d, Deg:%d\n", NBCmty.Len(), NI.GetDeg()); }
		//IAssert(NBCmty.Len() == NI.GetDeg() + 1);
		Phi = TAGMFastUtil.GetConductance(G, NBCmty, Edges);
	  }
	  //NCPhiH.AddDat(u, Phi);
	  NIdPhiV.Add(TFltIntPr(Phi, NI.GetId()));
	}
	System.out.printf("conductance computation completed [%s]\n", RunTm.GetTmStr());
	fflush(stdout);
  }

}

public class TAGMFast
{ //sparse AGM-fast with coordinate ascent
  private TPt<TUNGraph> G = new TPt<TUNGraph>(); //graph to fit
  private TVec<THash<TInt, TFlt>> F = new TVec<THash<TInt, TFlt>>(); // membership for each user (Size: Nodes * Coms)
  private TRnd Rnd = new TRnd(); // random number generator
  private TVec<TInt> NIDV = new TVec<TInt>(); // original node ID vector
  private TFlt RegCoef = new TFlt(); //Regularization coefficient when we fit for P_c +: L1, -: L2
  private TVec<TFlt> SumFV = new TVec<TFlt>(); // sum_u F_uc for each community c. Needed for efficient calculation
  private TInt NumComs = new TInt(); // number of communities
  private TVec<THashSet<TInt>> HOVIDSV = new TVec<THashSet<TInt>>(); //NID pairs to hold out for cross validation
  public TFlt MinVal = new TFlt(); // minimum value of F (0)
  public TFlt MaxVal = new TFlt(); // maximum value of F (for numerical reason)
  public TFlt NegWgt = new TFlt(); // weight of negative example (a pair of nodes without an edge)
  public TFlt PNoCom = new TFlt(); // base probability \varepsilon (edge probability between a pair of nodes sharing no community
  public TBool DoParallel = new TBool(); // whether to use parallelism for computation

  public TAGMFast(TPt<TUNGraph> GraphPt, int InitComs)
  {
	  this(GraphPt, InitComs, 0);
  }
//C++ TO JAVA CONVERTER NOTE: Java does not allow default values for parameters. Overloaded methods are inserted above:
//ORIGINAL LINE: TAGMFast(const TPt<TUNGraph>& GraphPt, const int& InitComs, const int RndSeed = 0): Rnd(RndSeed), RegCoef(0), MinVal(0.0), MaxVal(1000.0), NegWgt(1.0)
  public TAGMFast(TPt<TUNGraph> GraphPt, int InitComs, int RndSeed)
  {
	  this.Rnd = new TRnd(RndSeed);
	  this.RegCoef = new TFlt(0);
	  this.MinVal = new TFlt(0.0);

//
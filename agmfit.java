//====================================================================================================
//The Free Edition of C++ to Java Converter limits conversion output to 100 lines per file.

//To subscribe to the Premium Edition, visit our website:
//https://www.tangiblesoftwaresolutions.com/order/order-cplus-to-java.html
//====================================================================================================

import java.io.*;

/////////////////////////////////////////////////
/// Fitting the Affilialiton Graph Model (AGM).
public class TAGMFit implements Closeable
{
  private TPt<TUNGraph> G = new TPt<TUNGraph>(); ///< Graph to fit.
  private TVec<THashSet<TInt>> CIDNSetV = new TVec<THashSet<TInt>>(); ///< Community ID -> Member Node ID Sets.
  private THash<TPair<TInt, TInt>,THashSet<TInt>> EdgeComVH = new THash<TPair<TInt, TInt>,THashSet<TInt>>(); ///< Edge -> Shared Community ID Set.
  private THash<TInt, THashSet<TInt>> NIDComVH = new THash<TInt, THashSet<TInt>>(); ///< Node ID -> Communitie IDs the node belongs to.
  private TVec<TInt> ComEdgesV = new TVec<TInt>(); ///< The number of edges in each community.
  private TFlt PNoCom = new TFlt(); ///< Probability of edge when two nodes share no community (epsilon in the paper).
  private TVec<TFlt> LambdaV = new TVec<TFlt>(); ///< Parametrization of P_c (edge probability in community c), P_c = 1 - exp(-lambda).
  private TRnd Rnd = new TRnd();
  private THash<TPair<TInt, TInt>,TFlt> NIDCIDPrH = new THash<TPair<TInt, TInt>,TFlt>(); ///< <Node ID, Community ID> pairs (for sampling MCMC moves).
  private THash<TPair<TInt, TInt>,TInt> NIDCIDPrS = new THash<TPair<TInt, TInt>,TInt>(); ///< <Node ID, Community ID> pairs (for sampling MCMC moves).
  private TFlt MinLambda = new TFlt(); ///< Minimum value of regularization parameter lambda (default = 1e-5).
  private TFlt MaxLambda = new TFlt(); ///< Maximum value of regularization parameter lambda (default = 10).
  private TFlt RegCoef = new TFlt(); ///< Regularization parameter when we fit for P_c (for finding # communities).
  private TInt BaseCID = new TInt(); ///< ID of the Epsilon-community (in case we fit P_c of the epsilon community). We do not fit for the Epsilon-community in general.

  public TAGMFit()
  {
  }
  public final void close()
  {
  }
  /// COMMENT. Use @Param to describribe parameters.
  public TAGMFit(TPt<TUNGraph> GraphPt, TVec<TVec<TInt>> CmtyVVPt)
  {
	  this(GraphPt, CmtyVVPt, 0);
  }
//C++ TO JAVA CONVERTER NOTE: Java does not allow default values for parameters. Overloaded methods are inserted above:
//ORIGINAL LINE: TAGMFit(const TPt<TUNGraph>& GraphPt, const TVec<TVec<TInt>>& CmtyVVPt, const int RndSeed = 0): G(GraphPt), PNoCom(0.0), Rnd(RndSeed), MinLambda(0.00001), MaxLambda(10.0), RegCoef(0), BaseCID(-1)
  public TAGMFit(TPt<TUNGraph> GraphPt, TVec<TVec<TInt>> CmtyVVPt, int RndSeed)
  {
	  this.G = new TPt<TUNGraph>(GraphPt.functorMethod);
	  this.PNoCom = new TFlt(0.0);
	  this.Rnd = new TRnd(RndSeed);
	  this.MinLambda = new TFlt(0.00001);
	  this.MaxLambda = new TFlt(10.0);
	  this.RegCoef = new TFlt(0);
	  this.BaseCID = new TInt(-1);
	  SetCmtyVV(CmtyVVPt);
  }
  /// COMMENT. Use @Param to describribe parameters.
  public TAGMFit(TPt<TUNGraph> GraphPt, int InitComs)
  {
	  this(GraphPt, InitComs, 0);
  }
//C++ TO JAVA CONVERTER NOTE: Java does not allow default values for parameters. Overloaded methods are inserted above:
//ORIGINAL LINE: TAGMFit(const TPt<TUNGraph>& GraphPt, const int InitComs, const int RndSeed = 0): G(GraphPt), PNoCom(0.0), Rnd(RndSeed), MinLambda(0.00001), MaxLambda(10.0), RegCoef(0), BaseCID(-1)
  public TAGMFit(TPt<TUNGraph> GraphPt, int InitComs, int RndSeed)
  {
	  this.G = new TPt<TUNGraph>(GraphPt.functorMethod);
	  this.PNoCom = new TFlt(0.0);
	  this.Rnd = new TRnd(RndSeed);
	  this.MinLambda = new TFlt(0.00001);
	  this.MaxLambda = new TFlt(10.0);
	  this.RegCoef = new TFlt(0);
	  this.BaseCID = new TInt(-1);
	  NeighborComInit(InitComs);
  } //RandomInitCmtyVV(InitComs);  }
  /// COMMENT. Use @Param to describribe parameters.
  public TAGMFit(TPt<TUNGraph> GraphPt, TVec<TVec<TInt>> CmtyVVPt, TRnd RndPt)
  {
	  this.G = new TPt<TUNGraph>(GraphPt.functorMethod);
	  this.PNoCom = new TFlt(0.0);
	  this.Rnd = new TRnd(RndPt);
	  this.MinLambda = new TFlt(0.00001);
	  this.MaxLambda = new TFlt(10.0);
	  this.RegCoef = new TFlt(0);
	  this.BaseCID = new TInt(-1);
	  SetCmtyVV(CmtyVVPt);
  }

  /////////////////////////////////////////////////
  // AGM fitting

  public final void Save(TSOut SOut)
  {
	G.dereference().Save(SOut);
	CIDNSetV.Save(SOut);
	EdgeComVH.Save(SOut);
	NIDComVH.Save(SOut);
	ComEdgesV.Save(SOut);
	PNoCom.Save(SOut);
	LambdaV.Save(SOut);
	NIDCIDPrH.Save(SOut);
	NIDCIDPrS.Save(SOut);
	MinLambda.Save(SOut);
	MaxLambda.Save(SOut);
	RegCoef.Save(SOut);
	BaseCID.Save(SOut);
  }

  public final void Load(TSIn SIn)
  {
	  Load(SIn, 0);
  }
//C++ TO JAVA CONVERTER NOTE: Java does not allow default values for parameters. Overloaded methods are inserted above:
//ORIGINAL LINE: void Load(TSIn& SIn, const int& RndSeed = 0)
  public final void Load(TSIn SIn, int RndSeed)
  {
//C++ TO JAVA CONVERTER TODO TASK: The following line was determined to be a copy assignment (rather than a reference assignment) - this should be verified and a 'copyFrom' method should be created:
//ORIGINAL LINE: G = TUNGraph::Load(SIn);
	G.copyFrom(TUNGraph.Load.functorMethod(SIn));
	CIDNSetV.Load(SIn);
	EdgeComVH.Load(SIn);
	NIDComVH.Load(SIn);
	ComEdgesV.Load(SIn);
	PNoCom.Load(SIn);
	LambdaV.Load(SIn);
	NIDCIDPrH.Load(SIn);
	NIDCIDPrS.Load(SIn);
	MinLambda.Load(SIn);
	MaxLambda.Load(SIn);
	RegCoef.Load(SIn);
	BaseCID.Load(SIn);
	Rnd.PutSeed(RndSeed);
  }

  /// Randomly initialize bipartite community affiliation graph.

  // Randomly initialize bipartite community affiliation graph.
  public final void RandomInitCmtyVV(int InitComs, double ComSzAlpha, double MemAlpha, int MinComSz, int MaxComSz, int MinMem)
  {
	  RandomInitCmtyVV(InitComs, ComSzAlpha, MemAlpha, MinComSz, MaxComSz, MinMem, 10);
  }
  public final void RandomInitCmtyVV(int InitComs, double ComSzAlpha, double MemAlpha, int MinComSz, int MaxComSz)
  {
	  RandomInitCmtyVV(InitComs, ComSzAlpha, MemAlpha, MinComSz, MaxComSz, 1, 10);
  }
  public final void RandomInitCmtyVV(int InitComs, double ComSzAlpha, double MemAlpha, int MinComSz)
  {
	  RandomInitCmtyVV(InitComs, ComSzAlpha, MemAlpha, MinComSz, 200, 1, 10);
  }
  public final void RandomInitCmtyVV(int InitComs, double ComSzAlpha, double MemAlpha)
  {
	  RandomInitCmtyVV(InitComs, ComSzAlpha, MemAlpha, 8, 200, 1, 10);
  }
  public final void RandomInitCmtyVV(int InitComs, double ComSzAlpha)
  {
	  RandomInitCmtyVV(InitComs, ComSzAlpha, 1.8, 8, 200, 1, 10);
  }
  public final void RandomInitCmtyVV(int InitComs)
  {
	  RandomInitCmtyVV(InitComs, 1.3, 1.8, 8, 200, 1, 10);
  }
//C++ TO JAVA CONVERTER NOTE: Java does not allow default values for parameters. Overloaded methods are inserted above:
//ORIGINAL LINE: void RandomInitCmtyVV(const int InitComs, const double ComSzAlpha = 1.3, const double MemAlpha = 1.8, const int MinComSz = 8, const int MaxComSz = 200, const int MinMem = 1, const int MaxMem = 10)
  public final void RandomInitCmtyVV(int InitComs, double ComSzAlpha, double MemAlpha, int MinComSz, int MaxComSz, int MinMem, int MaxMem)
  {
	TVec<TVec<TInt>> InitCmtyVV = new TVec<TVec<TInt>>(InitComs, 0);
	TAGMUtil.GenCmtyVVFromPL(InitCmtyVV, G.functorMethod, G.dereference().GetNodes(), InitComs, ComSzAlpha, MemAlpha, MinComSz, MaxComSz, MinMem, MaxMem, Rnd);
	SetCmtyVV(InitCmtyVV);
  }

  /// Add Epsilon community (base community which includes all nodes) into community affiliation graph. This means that we will later fit the value of epsilon.

  // Add epsilon community (base community which includes all nodes) into community affiliation graph. It means that we fit for epsilon.
  public final void AddBaseCmty()
  {
	TVec<TVec<TInt>> CmtyVV = new TVec<TVec<TInt>>();
	GetCmtyVV(CmtyVV);

//====================================================================================================
//End of the allowed output for the Free Edition of C++ to Java Converter.

//To subscribe to the Premium Edition, visit our website:
//https://www.tangiblesoftwaresolutions.com/order/order-cplus-to-java.html
//====================================================================================================

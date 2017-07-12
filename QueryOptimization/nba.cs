using System;
		using System.Collections.Generic;
		using System.Linq;
		using System.Text;
		using System.Threading.Tasks;
		using System.IO;
		using System.Reflection;
		using MicrosoftResearch.Infer.Models;
		using MicrosoftResearch.Infer;
		using MicrosoftResearch.Infer.Distributions;
		using MicrosoftResearch.Infer.Maths;

		namespace BayesianInference
		{
		public class nbaModel 
			{

		public int[] StatesForAllAttrs=new int[5];
		public Variable<int> NumberofExamples;
		public int NumberofNodes = 5;
		//primary random variables
		public VariableArray<int> Attr0;
		public VariableArray<int> Attr1;
		public VariableArray<int> Attr2;
		public VariableArray<int> Attr3;
		public VariableArray<int> Attr4;
		//parameters
		public Variable<Vector> ProbAttr0;
		public VariableArray<Vector> CPTAttr1;
		public VariableArray<Vector> CPTAttr2;
		public VariableArray<Vector> CPTAttr3;
		public VariableArray<Vector> CPTAttr4;
		//prior distribution
		public Variable<Dirichlet> ProbAttr0Prior;
		public VariableArray<Dirichlet> CPTAttr1Prior;
		public VariableArray<Dirichlet> CPTAttr2Prior;
		public VariableArray<Dirichlet> CPTAttr3Prior;
		public VariableArray<Dirichlet> CPTAttr4Prior;
		//posterior distribution
		public Dirichlet ProbAttr0Posterior;
		public Dirichlet[] CPTAttr1Posterior;
		public Dirichlet[] CPTAttr2Posterior;
		public Dirichlet[] CPTAttr3Posterior;
		public Dirichlet[] CPTAttr4Posterior;
		public InferenceEngine Engine = new InferenceEngine();

		//model construction
		public nbaModel(string fname)
		{
		int i = 0;
		NumberofExamples = Variable.New<int>();
		Range N = new Range(NumberofExamples);

		try
		{
		FileStream fs = new FileStream(fname, FileMode.Open, FileAccess.Read, FileShare.Read);
		StreamReader sr = new StreamReader(fs);
		string str = "";
		i=0;
		while ((str = sr.ReadLine()) != null)
		{
		StatesForAllAttrs[i++] = Int32.Parse(str);
		}
		}
		catch (IOException e)
		{
		Console.Write(e.ToString());
		}


Range range0 = new Range(StatesForAllAttrs[0]);
Range range1 = new Range(StatesForAllAttrs[1]);
Range range2 = new Range(StatesForAllAttrs[2]);
Range range3 = new Range(StatesForAllAttrs[3]);
Range range4 = new Range(StatesForAllAttrs[4]);
Attr0=Variable.Array<int>(N);
		ProbAttr0Prior=Variable.New<Dirichlet>();
		ProbAttr0=Variable<Vector>.Random(ProbAttr0Prior);
		ProbAttr0.SetValueRange(range0);
		Attr0[N] = Variable.Discrete(ProbAttr0).ForEach(N);

CPTAttr1Prior = Variable.Array<Dirichlet>(range3);
		CPTAttr1 = Variable.Array<Vector>(range3);
		CPTAttr1[range3] = Variable<Vector>.Random(CPTAttr1Prior[range3]);
		CPTAttr1.SetValueRange(range1);

CPTAttr2Prior = Variable.Array<Dirichlet>(range3);
		CPTAttr2 = Variable.Array<Vector>(range3);
		CPTAttr2[range3] = Variable<Vector>.Random(CPTAttr2Prior[range3]);
		CPTAttr2.SetValueRange(range2);

CPTAttr3Prior = Variable.Array<Dirichlet>(range0);
		CPTAttr3 = Variable.Array<Vector>(range0);
		CPTAttr3[range0] = Variable<Vector>.Random(CPTAttr3Prior[range0]);
		CPTAttr3.SetValueRange(range3);

CPTAttr4Prior = Variable.Array<Dirichlet>(range1);
		CPTAttr4 = Variable.Array<Vector>(range1);
		CPTAttr4[range1] = Variable<Vector>.Random(CPTAttr4Prior[range1]);
		CPTAttr4.SetValueRange(range4);

Attr3 = AddChildFromOneParent(Attr0, CPTAttr3);
Attr1 = AddChildFromOneParent(Attr3, CPTAttr1);
Attr2 = AddChildFromOneParent(Attr3, CPTAttr2);
Attr4 = AddChildFromOneParent(Attr1, CPTAttr4);
		}
		public void LearnParameters(int[][] data, int num)
				{
NumberofExamples.ObservedValue = num; 
Dirichlet probattr0prior = Dirichlet.Uniform(StatesForAllAttrs[0]);
		Attr0.ObservedValue = data[0];
		ProbAttr0Prior.ObservedValue = probattr0prior;
Dirichlet[] unifarray1 = Enumerable.Repeat(Dirichlet.Uniform(StatesForAllAttrs[1]), StatesForAllAttrs[3]).ToArray();
		Attr1.ObservedValue = data[1];
		CPTAttr1Prior.ObservedValue = unifarray1;
Dirichlet[] unifarray2 = Enumerable.Repeat(Dirichlet.Uniform(StatesForAllAttrs[2]), StatesForAllAttrs[3]).ToArray();
		Attr2.ObservedValue = data[2];
		CPTAttr2Prior.ObservedValue = unifarray2;
Dirichlet[] unifarray3 = Enumerable.Repeat(Dirichlet.Uniform(StatesForAllAttrs[3]), StatesForAllAttrs[0]).ToArray();
		Attr3.ObservedValue = data[3];
		CPTAttr3Prior.ObservedValue = unifarray3;
Dirichlet[] unifarray4 = Enumerable.Repeat(Dirichlet.Uniform(StatesForAllAttrs[4]), StatesForAllAttrs[1]).ToArray();
		Attr4.ObservedValue = data[4];
		CPTAttr4Prior.ObservedValue = unifarray4;
//inference
ProbAttr0Posterior = Engine.Infer<Dirichlet>(ProbAttr0);
CPTAttr1Posterior = Engine.Infer<Dirichlet[]>(CPTAttr1);
CPTAttr2Posterior = Engine.Infer<Dirichlet[]>(CPTAttr2);
CPTAttr3Posterior = Engine.Infer<Dirichlet[]>(CPTAttr3);
CPTAttr4Posterior = Engine.Infer<Dirichlet[]>(CPTAttr4);
		}

public Discrete[] ProbabilityComputation(int[] dp, int target)
		{
		NumberofExamples.ObservedValue=1;
if (dp[0]!=-1)
				Attr0.ObservedValue = new int[] { dp[0] };
				else
				Attr0.ClearObservedValue();
				ProbAttr0Prior=ProbAttr0Posterior;
if (dp[1]!=-1)
				Attr1.ObservedValue = new int[] { dp[1] };
				else
				Attr1.ClearObservedValue();
				CPTAttr1Prior.ObservedValue = CPTAttr1Posterior;
if (dp[2]!=-1)
				Attr2.ObservedValue = new int[] { dp[2] };
				else
				Attr2.ClearObservedValue();
				CPTAttr2Prior.ObservedValue = CPTAttr2Posterior;
if (dp[3]!=-1)
				Attr3.ObservedValue = new int[] { dp[3] };
				else
				Attr3.ClearObservedValue();
				CPTAttr3Prior.ObservedValue = CPTAttr3Posterior;
if (dp[4]!=-1)
				Attr4.ObservedValue = new int[] { dp[4] };
				else
				Attr4.ClearObservedValue();
				CPTAttr4Prior.ObservedValue = CPTAttr4Posterior;
string str = "Attr" + target.ToString();
	Type t = this.GetType();
	BindingFlags flag = BindingFlags.Public | BindingFlags.Instance;
	FieldInfo finfo = t.GetField(str, flag);
	if (finfo == null)
	{
	Console.ReadKey();
	}
	object obj = finfo.GetValue(this);
	var posterior = Engine.Infer<Discrete[]>((VariableArray<int>)obj);
	return posterior;
}
public static VariableArray<int> AddChildFromOneParent(
	VariableArray<int> parent,
	VariableArray<Vector> cpt)
	{
	var n = parent.Range;
	var child = Variable.Array<int>(n);
	using (Variable.ForEach(n))
	using (Variable.Switch(parent[n]))
	child[n] = Variable.Discrete(cpt[parent[n]]);
	return child;
	}

public static VariableArray<int> AddChildFromTwoParents(
	VariableArray<int> parent1,
	VariableArray<int> parent2,
	VariableArray<VariableArray<Vector>, Vector[][]> cpt)
	{
	var n = parent1.Range;
	var child = Variable.Array<int>(n);
	using (Variable.ForEach(n))
	using (Variable.Switch(parent1[n]))
	using (Variable.Switch(parent2[n]))
	child[n] = Variable.Discrete(cpt[parent1[n]][parent2[n]]);
	return child;
	}
	}
public class nba
		{
		public void Run()
		{
string nf = "C:\\Users\\TLD7040A\\Desktop\\BayesianNetwork\\banjo.2.2.0\\data\\release2.0\\static\\input\\nba.5d.5000.i50.attrs.txt", df = "C:\\Users\\TLD7040A\\Desktop\\BayesianNetwork\\banjo.2.2.0\\data\\release2.0\\static\\input\\nba.5d.5000.i50.output.txt";//nodefile, bayesian network file, data file
		int dnum = 5000,length = 0, i = 0, j = 0;
		nbaModel m = new nbaModel(nf);
		length = m.StatesForAllAttrs.Length;
		int[][] data = new int[length][];
		for (i = 0; i < length; i++)
		data[i] = new int[dnum];
		try
		{
		FileStream fs = new FileStream(df, FileMode.Open, FileAccess.Read, FileShare.Read);
		StreamReader sr = new StreamReader(fs);
		string str = "";
		i = 0;
		while (str != null)
		{
		str = sr.ReadLine();
		if (str == null)
		break;
		string[] strAry = str.Split('	');
		for (j = 0; j<strAry.Length; j++)
		{
		data[j][i] = Int32.Parse(strAry[j]);
		}
		i++;
		}
		}
		catch (IOException e)
		{
		Console.Write(e.ToString());
		}
		m.Engine.Algorithm = new ExpectationPropagation();
		m.LearnParameters(data, dnum);
		int[] dp = new int[]{7, -1, 1, 2, 2};
		Discrete[] v = m.ProbabilityComputation(dp, 1);
		for (i = 0; i<v[0].Dimension; i++)
		{
		Console.WriteLine("{0:0.00}", v[0].GetProbs()[i]);
				}
}
}
}

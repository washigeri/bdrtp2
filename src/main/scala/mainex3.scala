import logic._
import org.apache.spark.graphx.{Edge, EdgeContext, Graph, VertexId}
import org.apache.spark.{SparkConf, SparkContext}


object mainex3 {

  val Conf: SparkConf = new SparkConf().setAppName("BDRTP2ex2").setMaster("local[*]")

  def main(args: Array[String]): Unit = {
    var sc= new SparkContext(Conf);
    val input = sc.textFile("log.txt")

    val splitedLines = input.flatMap(line => line.split(" ")).map(word=> (word.toLowerCase,1)).groupByKey().map(t=>(t._1,t._2.sum))
  //  var res=  List( 1, 2, 3, 4 ).reduce( (x,y) => x + y )
   var res= splitedLines.collect()


    println(splitedLines);
  }
}

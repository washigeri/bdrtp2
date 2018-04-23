import logic._
import org.apache.spark.graphx.{Edge, EdgeContext, Graph, VertexId}
import org.apache.spark.{SparkConf, SparkContext}


object mainex3 {

  val Conf: SparkConf = new SparkConf().setAppName("BDRTP2ex2").setMaster("local[*]")

  def main(args: Array[String]): Unit = {
    var sc= new SparkContext(Conf);
      val tab1: Array[(Int,Int)]= Array((1,2),(3,4),(3,6),(5,6))
    val tab2: Array[(Int,Int)]= Array((3,9))
    val distData=sc.parallelize(tab1)
    val distData2=sc.parallelize(tab2)

    val testRDD= distData.join(distData2);
    val test2RDD=distData.leftOuterJoin(distData2)
    val test3RDD= distData.rightOuterJoin(distData2);
    val test4RDD=distData.fullOuterJoin(distData2)
    testRDD.collect().foreach(println)
    print("---- test1 \n")
    test2RDD.collect().foreach(println)
    print("---- test 2\n")
    test3RDD.collect().foreach(println)
    print("---- test 3\n")
    test4RDD.collect().foreach(println)
    print("---- test 4 \n")

  }
}

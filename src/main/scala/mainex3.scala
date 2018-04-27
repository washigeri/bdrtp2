import ex2.logic._
import ex2.monsters._
import org.apache.spark.graphx.{Edge, EdgeContext, Graph, VertexId}
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ArrayBuffer
import scala.math.{max, min}
import scala.util.Random

object mainex3 {

  val Conf: SparkConf = new SparkConf().setAppName("BDRTP2ex2").setMaster("local[*]")

  def main(args: Array[String]): Unit = {
    val sc = new SparkContext(Conf)
    val text=sc.textFile("log.txt");
    val res3=text.collect();
    val rdd1 = text.map(line=>line.split(" "))
    val rdd2 = text.flatMap(line=>line.split(" ")).map(word=>(word.toLowerCase(),1)).reduceByKey(_+_);

    val res=rdd1.collect();
    val res2=rdd2.collect();
    println("test");
  }

}

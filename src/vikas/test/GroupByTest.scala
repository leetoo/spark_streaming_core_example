import java.util.Random

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Usage: GroupByTest [numMappers] [numKVPairs] [KeySize] [numReducers]
  */
object GroupByTest {
  def main(args: Array[String]) {
    val sparkConf = new SparkConf().setAppName("GroupBy Test").setMaster("local[*]")

    var numMappers = if (args.length > 0) args(0).toInt else 2
    var numKVPairs = if (args.length > 1) args(1).toInt else 1000
    var valSize = if (args.length > 2) args(2).toInt else 1000
    var numReducers = if (args.length > 3) args(3).toInt else numMappers

    val sc = new SparkContext(sparkConf)

    val pairs1 = sc.parallelize(0 until numMappers, numMappers).flatMap { p =>
      val ranGen = new Random
      var arr1 = new Array[(Int, Array[Byte])](numKVPairs)
      for (i <- 0 until numKVPairs) {
        val byteArr = new Array[Byte](valSize)
        ranGen.nextBytes(byteArr)
        arr1(i) = (ranGen.nextInt(Int.MaxValue), byteArr)
      }
      arr1
    }.cache()
    // Enforce that everything has been calculated and in cache
    pairs1.count()

    println(pairs1.groupByKey(numReducers).count())
    readLine()
    sc.stop()
  }
}

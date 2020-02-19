package com.github.mkdika.vertxhelloworld

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.redis.client.Redis
import io.vertx.redis.client.RedisAPI
import io.vertx.redis.client.RedisOptions


class MainVerticle : AbstractVerticle() {

  override fun start(startPromise: Promise<Void>) {
    println("Start verticle...")
    val key = "abc"

//    val redis = Redis.createClient(vertx, RedisOptions())
//    redis.connect { r ->
//      if (r.succeeded()) {
//        val client = r.result()
//        val api = RedisAPI.api(client)
//        api.get(key) { result ->
//          if (result.succeeded() && result.result() != null) {
//            println("Result: abc:${result.result()}")
//          }else {
//            println("Result error: ${result.cause()}")
//          }
//        }
//      }else {
//        println("Result error: ${r.cause()}")
//      }
//    }


    val redisDao = RedisDao(vertx)
    val operation = redisDao.getBacklog(key).future()
    operation.setHandler {
      if (it.succeeded()) {
        println("Result: abc:${it.result()}")
      } else {
        println("Result error: ${it.cause()}")
      }
    }
  }
}

class TestUsecase {

  fun execute(id: Int): Future<String> {

    return Future.succeededFuture()
  }
}

class RedisDao(val vertx: Vertx) {

  fun getBacklog(key: String): Promise<String> {

    val promisex = Promise.promise<String>()
    val redis = Redis.createClient(vertx, RedisOptions())
    redis.connect { r ->
      if (r.succeeded()) {
        val client = r.result()
        val api = RedisAPI.api(client)
        api.get(key) { result ->
          if (result.succeeded() && result.result() != null) {
            promisex.complete(result.result().toString())
          }else {
            promisex.fail(result.cause())
          }
        }
      }else {
        promisex.fail(r.cause())
      }
    }
    return promisex
  }
}


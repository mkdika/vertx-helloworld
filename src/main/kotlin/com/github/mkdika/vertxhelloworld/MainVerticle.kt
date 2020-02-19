package com.github.mkdika.vertxhelloworld

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.redis.client.Redis
import io.vertx.redis.client.RedisAPI
import io.vertx.redis.client.RedisOptions

class MainVerticle : AbstractVerticle() {

  override fun start(startPromise: Promise<Void>) {
    println("Start verticle...")

    val key = "abc"
    val redisDao = RedisDao(vertx)
    redisDao.getBacklog(key)
      .future()
      .setHandler { result ->
        if (result.succeeded()) {
          println("Result: abc:${result.result()}")
        } else {
          println("Result error: ${result.cause()}")
        }
      }
  }
}

class RedisApi(val redisClient: Redis) {

  private val redisApi: RedisAPI = RedisAPI.api(redisClient)

  fun getRedisApi(): RedisAPI {
    return redisApi
  }
}


class RedisDao(val vertx: Vertx) {
  fun getBacklog(key: String): Promise<String> {

    val redisPromise = Promise.promise<Redis>()
    val resultPromise = Promise.promise<String>()
    Redis.createClient(vertx, RedisOptions())
      .connect { conn ->
        if (conn.succeeded()) {
          redisPromise.complete(conn.result())
        } else {
          redisPromise.fail(conn.cause())
        }
      }
    redisPromise
      .future()
      .setHandler { redisClient ->
        if (redisClient.succeeded()) {
          val redisApi = RedisApi(redisClient.result()).getRedisApi()
          redisApi.get(key) { result ->
            if (result.succeeded()) {
              resultPromise.complete(result.result().toString())
            }else {
              resultPromise.fail(result.cause())
            }
          }
        } else {
          resultPromise.fail(redisClient.cause())
        }
      }
    return resultPromise
  }
}

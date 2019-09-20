package examples.udemy.s8_AkkaHttp.ClientApi

import spray.json.DefaultJsonProtocol


case class IpInfo(ip: String)


object JsonProtocol extends DefaultJsonProtocol {

  implicit val format = jsonFormat1(IpInfo.apply)
}

import com.typesafe.config.ConfigFactory

package object core {
  val defaultConf = ConfigFactory.parseString("""
    spray.can.client.request-timeout = 5000ms
    spray.can.host-connector.max-retries = 5
    spray.can.host-connector.idle-timeout = infinite
    spray.can.host-connector.client.request-timeout = 5000ms
    spray.can.server.verbose-error-messages = on
    spray.can.host-connector.max-redirects = 3""")
}

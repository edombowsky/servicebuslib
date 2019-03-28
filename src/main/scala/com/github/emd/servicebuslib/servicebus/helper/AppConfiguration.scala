/*
 * Copyright (c) 2017-2019 ABB - All rights reserved.
 */

package com.github.emd.servicebuslib.servicebus.helper

import com.github.emd.servicebuslib.servicebus.SBContext
import com.github.emd.servicebuslib.servicebus.SBContext
import pureconfig.ConfigReader.Result
// Although Intellij IDEA says this is an unused import, if it is not here
// we will get two compile errors about unable to find implicit value for
// parameter reader when calling loadConfig [[https://github.com/pureconfig/pureconfig]]
import pureconfig.generic.auto._

case class AppConfiguration(language: String, connections: List[SBContext]) {
  /**
   * Get the list of connections defined in the configuration file.
   *
   * @return <code>List[String]</code> containing connection strings
   *         defined in the application configuration file, empty list
   *         if no connections were defined.
   */
  def connectionList: List[String] = connections map (_.name)

  /**
   * Get a ServiceBusContext given a tenant name.
   *
   * @param tenant name of tenant to find SBContext for
   *
   * @return Option[ServiceBus]
   */
  def serviceBusContext(tenant: String): Option[SBContext] =
    connections.find(p => p.name == tenant)

}

object AppConfiguration {

  /**
   * Initialise the applications configuration. Reads the configuration file
   * and populates application configurations structures for later retrieval.
   *
   * @return `AppConfiguration` if success else a
   *         `Failure` with details on what failed
   */
  def config: Result[AppConfiguration] = pureconfig.loadConfig[AppConfiguration]
}

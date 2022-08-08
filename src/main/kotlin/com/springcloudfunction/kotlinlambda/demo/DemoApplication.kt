package com.springcloudfunction.kotlinlambda.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.function.context.PollableBean
import org.springframework.context.annotation.Bean
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.messaging.support.ErrorMessage
import java.util.function.Consumer
import java.util.function.Supplier

data class Person(
	val name: String,
)

@SpringBootApplication
class DemoApplication {
	@Bean
	@PollableBean
	fun producePerson(): Supplier<Person> = Supplier {
		println("Sending person...")
		Person("person")
	}

	/*
	@Bean
	fun logPerson(): Consumer<Person> = Consumer { person ->
		throw java.lang.RuntimeException("Triggered an error")
	}
	 */

	@Bean
	fun logPerson(): (Person) -> Unit = { person ->
		throw RuntimeException("Triggered an error")
	}

	/**
	 * Expected behavior:
	 *  It should print "Received error message".
	 *
	 * Symptoms:
	 *  1. If move onErrorMessage(@ServiceActivator) to another @Component, it works.
	 *  2. If use java.util.function.Consumer as the return type of logPerson, it works.
	 *  3. If use Kotlin lambda as the return type of logPerson, it doesn't work.
	 *  4. We just could not put Kotlin lambda and @ServiceActivator in the same component.
	 */
	@ServiceActivator(inputChannel="errorChannel")
	fun onErrorMessage(errorMessage: ErrorMessage) {
		println("Received error message")
	}
}

fun main(args: Array<String>) {
	runApplication<DemoApplication>(*args)
}

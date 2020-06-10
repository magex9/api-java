package ca.magex.json.javadoc.samples;

import org.slf4j.Logger;
import java.time.Duration;

import ca.magex.json.javadoc.samples.CrudService;

import java.util.List;
import java.util.NoSuchElementException;

public class CrudServiceLoggerDelegate<K, T> implements CrudService<K, T> {
	
	private CrudService<K, T> delegate;
	
	private Logger logger;
	
	public CrudServiceLoggerDelegate(CrudService<K, T> delegate, Logger logger) {
		this.delegate = delegate;
		this.logger = logger;
	}
	
	@Override
	public T load(K id) throws NoSuchElementException {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling load(" + id + ")");
				T result = delegate.load(id);
				logger.trace("Executed load(" + id + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on load(" + id + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling load(" + id + ")");
				T result = delegate.load(id);
				logger.debug("Executed load(" + id + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on load(" + id + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling load(" + id + ")");
			return delegate.load(id);
		}
		else {
			return delegate.load(id);
		}
	}
	
	@Override
	public List<T> findAll() {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling findAll()");
				List<T> result = delegate.findAll();
				logger.trace("Executed findAll() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on findAll() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling findAll()");
				List<T> result = delegate.findAll();
				logger.debug("Executed findAll() in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on findAll() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling findAll()");
			return delegate.findAll();
		}
		else {
			return delegate.findAll();
		}
	}
	
	@Override
	public void update(T entity) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling update(" + entity + ")");
				delegate.update(entity);
				logger.trace("Executed update(" + entity + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return;
			}
			catch (Exception e) {
				logger.trace("Exception on update(" + entity + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling update(" + entity + ")");
				delegate.update(entity);
				logger.debug("Executed update(" + entity + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return;
			}
			catch (Exception e) {
				logger.debug("Exception on update(" + entity + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling update(" + entity + ")");
			delegate.update(entity);
		}
		else {
			delegate.update(entity);
		}
	}
	
}

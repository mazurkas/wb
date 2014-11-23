package com.whosenet.wb.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception - 资源不存在异常.
 *
 * @author <a href="http://ruo.whosenet.com">ruo.whosenet.com</>
 * @version 1.0
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -9208522773597070069L;

}
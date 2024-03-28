package com.se.board.domain.es;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EsBoard {

	private String id;
	private LocalDateTime createdDate;				// @timestamp  날짜는 타입을 무엇으로 하는게 좋을지...
	private String postId;
	private String fileId;
	private String title;
	private String content;
	private String writer;
	private boolean noticeYn;
	private boolean deleteYn;						// 현재 삭제하므로 사용안함
	private String savedFilename;
	private LocalDateTime modifiedDate;
	private LocalDateTime deletedDate;				// 현재 삭제하므로 사용안함

	public void setId() {
		this.id = this.postId + "_" + this.fileId ;
	}

	@Override
	public boolean equals(Object obj) {
		return ((EsBoard) obj).getId() != null && ((EsBoard) obj).getId() == this.id;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.id);
	}
}

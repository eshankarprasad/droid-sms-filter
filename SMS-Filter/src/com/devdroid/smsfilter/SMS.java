package com.devdroid.smsfilter;

/*
_id : 1
thread_id : 1
address : 3
person : 0
date : 1
date_sent : 1
protocol : 1
read : 1
status : 1
type : 1
reply_path_present : 1
subject : 0
body : 3
service_center : 3
locked : 1
error_code : 1
seen : 1
deletable : 1
hidden : 1
group_id : 0
group_type : 0
delivery_date : 0
app_id : 1
msg_id : 1
callback_number : 0
reserved : 1
pri : 1
teleservice_id : 1
link_url : 0
*/
public class SMS {
	
	private long id, thread_id, dateSent, protocol, read, status, type, replyPathPresent, locked, errorCode, seen, deletable, hidden, appId, msgId, reserved, pri, teleserviceId;
	private String date, address, person, subject, body, serviceCenter, groupId, groupType, deliveryDate, callbackNumber, linkUrl;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getThread_id() {
		return thread_id;
	}
	public void setThread_id(long thread_id) {
		this.thread_id = thread_id;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public long getDateSent() {
		return dateSent;
	}
	public void setDateSent(long dateSent) {
		this.dateSent = dateSent;
	}
	public long getProtocol() {
		return protocol;
	}
	public void setProtocol(long protocol) {
		this.protocol = protocol;
	}
	public long getRead() {
		return read;
	}
	public void setRead(long read) {
		this.read = read;
	}
	public long getStatus() {
		return status;
	}
	public void setStatus(long status) {
		this.status = status;
	}
	public long getType() {
		return type;
	}
	public void setType(long type) {
		this.type = type;
	}
	public long getReplyPathPresent() {
		return replyPathPresent;
	}
	public void setReplyPathPresent(long replyPathPresent) {
		this.replyPathPresent = replyPathPresent;
	}
	public long getLocked() {
		return locked;
	}
	public void setLocked(long locked) {
		this.locked = locked;
	}
	public long getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(long errorCode) {
		this.errorCode = errorCode;
	}
	public long getSeen() {
		return seen;
	}
	public void setSeen(long seen) {
		this.seen = seen;
	}
	public long getDeletable() {
		return deletable;
	}
	public void setDeletable(long deletable) {
		this.deletable = deletable;
	}
	public long getHidden() {
		return hidden;
	}
	public void setHidden(long hidden) {
		this.hidden = hidden;
	}
	public long getAppId() {
		return appId;
	}
	public void setAppId(long appId) {
		this.appId = appId;
	}
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
	public long getReserved() {
		return reserved;
	}
	public void setReserved(long reserved) {
		this.reserved = reserved;
	}
	public long getPri() {
		return pri;
	}
	public void setPri(long pri) {
		this.pri = pri;
	}
	public long getTeleserviceId() {
		return teleserviceId;
	}
	public void setTeleserviceId(long teleserviceId) {
		this.teleserviceId = teleserviceId;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPerson() {
		return person;
	}
	public void setPerson(String person) {
		this.person = person;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getServiceCenter() {
		return serviceCenter;
	}
	public void setServiceCenter(String serviceCenter) {
		this.serviceCenter = serviceCenter;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getGroupType() {
		return groupType;
	}
	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}
	public String getDeliveryDate() {
		return deliveryDate;
	}
	public void setDeliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
	public String getCallbackNumber() {
		return callbackNumber;
	}
	public void setCallbackNumber(String callbackNumber) {
		this.callbackNumber = callbackNumber;
	}
	public String getLinkUrl() {
		return linkUrl;
	}
	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}
	@Override
	public String toString() {
		return "SMS [id=" + id + ", thread_id=" + thread_id + ", date=" + date
				+ ", dateSent=" + dateSent + ", protocol=" + protocol
				+ ", read=" + read + ", status=" + status + ", type=" + type
				+ ", replyPathPresent=" + replyPathPresent + ", locked="
				+ locked + ", errorCode=" + errorCode + ", seen=" + seen
				+ ", deletable=" + deletable + ", hidden=" + hidden
				+ ", appId=" + appId + ", msgId=" + msgId + ", reserved="
				+ reserved + ", pri=" + pri + ", teleserviceId="
				+ teleserviceId + ", address=" + address + ", person=" + person
				+ ", subject=" + subject + ", body=" + body
				+ ", serviceCenter=" + serviceCenter + ", groupId=" + groupId
				+ ", groupType=" + groupType + ", deliveryDate=" + deliveryDate
				+ ", callbackNumber=" + callbackNumber + ", linkUrl=" + linkUrl
				+ "]";
	}
	
}

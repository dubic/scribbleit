/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.application;

import com.dubic.module.el.data.Log;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 *
 * @author dubem
 */
@Entity
@Table(name = "log_model")
@NamedQueries({
    @NamedQuery(name = "log.exists", query = "SELECT l FROM LogModel l WHERE l.className = :cls AND l.method = :mtd AND l.lineNo = :lnum")
})
public class LogModel implements Serializable {

    private Long id;
    private String level;
    private String message;
    private String threadName;
    private Date logdate = new Date();
    private String username;
    private String className;
    private String method;
    private String lineNo;
    private String exceptionTrace;
    private int occurrence = 1;

    public LogModel() {
    }

    public LogModel(Log log) {
        this.className = log.getClassName();
        this.exceptionTrace = log.getExceptionTrace();
        this.level = log.getLevel();
        this.lineNo = log.getLineNo();
        this.logdate = log.getDate();
        this.message = log.getMessage();
        this.method = log.getMethod();
        this.threadName = log.getThreadName();

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "log_level")
    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Column(name = "msg")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Column(name = "thread")
    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    @Column(name = "log_dt")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getLogdate() {
        return logdate;
    }

    public void setLogdate(Date logdate) {
        this.logdate = logdate;
    }

    @Column(name = "username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "class_name")
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Column(name = "line")
    public String getLineNo() {
        return lineNo;
    }

    public void setLineNo(String lineNo) {
        this.lineNo = lineNo;
    }

    @Column(name = "method")
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Lob
    @Column(length = 300000000, name = "stacktrace")
    public String getExceptionTrace() {
        return exceptionTrace;
    }

    public void setExceptionTrace(String exceptionTrace) {
        this.exceptionTrace = exceptionTrace;
    }

    @Column(name = "occurrence")
    public int getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(int occurrence) {
        this.occurrence = occurrence;
    }

    public void addOccurrence(int o) {
        this.occurrence = this.occurrence + o;
    }

    @Override
    public String toString() {
        return "LogModel{" + "id=" + id + ", level=" + level + ", message=" + message + ", threadName=" + threadName + ", logdate=" + logdate + ", username=" + username + ", className=" + className + ", method=" + method + ", lineNo=" + lineNo + ", occurrence=" + occurrence + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LogModel other = (LogModel) obj;
        if ((this.username == null) ? (other.username != null) : !this.username.equals(other.username)) {
            return false;
        }
        if ((this.className == null) ? (other.className != null) : !this.className.equals(other.className)) {
            return false;
        }
        if ((this.method == null) ? (other.method != null) : !this.method.equals(other.method)) {
            return false;
        }
        if ((this.lineNo == null) ? (other.lineNo != null) : !this.lineNo.equals(other.lineNo)) {
            return false;
        }
        return true;
    }

}

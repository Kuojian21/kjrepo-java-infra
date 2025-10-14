package com.kjrepo.infra.runner.sch.quatz;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;

public class QuatzJobDetail implements JobDetail {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final JobDetail jobDetail;
	private final Job job;
	private final Object[] args;

	public QuatzJobDetail(JobDetail jobDetail, Job job, Object[] args) {
		this.jobDetail = jobDetail;
		this.job = job;
		this.args = args;
	}

	@Override
	public String getDescription() {
		return this.jobDetail.getDescription();
	}

	@Override
	public Class<? extends Job> getJobClass() {
		return this.jobDetail.getJobClass();
	}

	@Override
	public JobDataMap getJobDataMap() {
		return this.jobDetail.getJobDataMap();
	}

	@Override
	public JobKey getKey() {
		return this.jobDetail.getKey();
	}

	@Override
	public boolean isConcurrentExectionDisallowed() {
		return this.jobDetail.isConcurrentExectionDisallowed();
	}

	@Override
	public boolean isDurable() {
		return this.jobDetail.isDurable();
	}

	@Override
	public boolean isPersistJobDataAfterExecution() {
		return this.jobDetail.isPersistJobDataAfterExecution();
	}

	@Override
	public boolean requestsRecovery() {
		return this.jobDetail.requestsRecovery();
	}

	public Job getJob() {
		return this.job;
	}

	public Object[] getArgs() {
		return args;
	}

	public JobBuilder getJobBuilder() {
		JobBuilder b = QuatzJobDetailBuilder.job(this.job).ofType(getJobClass()).requestRecovery(requestsRecovery())
				.storeDurably(isDurable()).usingJobData(getJobDataMap()).withDescription(getDescription())
				.withIdentity(getKey());
		return b;
	}

	@Override
	public Object clone() {
		return new QuatzJobDetail((JobDetail) this.jobDetail.clone(), this.job, this.args);
	}

	@Override
	public String toString() {
		return "JobDetail '" + this.jobDetail.getKey().getGroup() + "." + this.jobDetail.getKey().getName()
				+ "':  jobClass: '" + ((getJobClass() == null) ? null : getJobClass().getName())
				+ " concurrentExectionDisallowed: " + isConcurrentExectionDisallowed()
				+ " persistJobDataAfterExecution: " + isPersistJobDataAfterExecution() + " isDurable: " + isDurable()
				+ " requestsRecovers: " + requestsRecovery();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof QuatzJobDetail)) {
			return false;
		}

		QuatzJobDetail other = (QuatzJobDetail) obj;

		if (other.getKey() == null || getKey() == null)
			return false;

		if (!other.getKey().equals(getKey())) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return getKey().hashCode();
	}

}

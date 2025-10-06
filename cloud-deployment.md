# Cloud Deployment Analysis

This document analyzes two potential cloud deployment options for the Patient Management project, which requires four always-on Spring Boot services and two always-on PostgreSQL databases.

---

## Option 1: Google Cloud Run (Serverless)

A fully-managed serverless platform that abstracts away the underlying infrastructure.

**[Pricing Details](https://cloud.google.com/run/pricing?hl=en)**

### Requirements:
- **Cloud Run Services:** Configure four services. To meet the "always on" requirement, each service must be configured with a minimum of 1 instance (`min-instances=1`).
- **Cloud SQL:** Create and configure a Cloud SQL for PostgreSQL instance to host the two databases.
- **Billing Management:** Set up billing alerts and budgets to monitor costs closely.

### Pros:
- **Managed Infrastructure:** Google handles the underlying infrastructure, patching, and scaling, reducing operational overhead.
- **Automatic Scaling:** Can automatically scale the number of container instances based on traffic, although the project requires a constant minimum.
- **High Availability:** Provides a robust, fault-tolerant, and industry-ready solution with built-in redundancy.

### Cons:
- **Cost:** The "always on" requirement (`min-instances=1`) for four services will likely **exceed the free tier**. Serverless is most cost-effective for workloads that can scale to zero. You will incur predictable monthly costs.
- **Complex Billing:** The pay-per-use pricing model can be complex. Careful monitoring is essential to avoid unexpected bills.
- **Database Costs:** Cloud SQL is a separate, billable service and can be a significant portion of the total cost.

---

## Option 2: Oracle Cloud ARM Compute (IaaS)

A virtual machine (Infrastructure as a Service) where you have full control over the operating system and installed software.

**[Always Free Tier Details](https.oracle.com/cloud/free/)**

### Requirements:
- **VM Provisioning:** Reserve and configure an "Always Free" Ampere A1 Compute instance (up to 4 OCPUs and 24 GB of memory).
- **Manual Software Installation:** Connect via SSH to install and configure all necessary software (e.g., Docker, Docker Compose, Nginx).
- **Manual Configuration:** Set up the firewall, a reverse proxy (Nginx) for routing traffic to your services, DNS, and SSL/TLS certificates (e.g., via Let's Encrypt).
- **Ongoing Maintenance:** You are responsible for all system administration, including OS updates, security patches, and monitoring.

### Pros:
- **Cost-Effective:** The "Always Free" tier is generous and can likely host the entire application stack with **no monetary cost**.
- **Excellent Learning Experience:** Provides deep, hands-on experience with system administration, networking, and security.
- **Full Control:** Complete control over the environment and software stack.
- **Centralized Architecture:** All components (apps, databases, proxy) run on a single, unified instance.

### Cons:
- **Single Point of Failure:** The entire application stack resides on a single VM. If the VM goes down, everything goes down. Not fault-tolerant.
- **Limited Scalability:** Scaling requires manually provisioning a larger VM. No automatic scaling.
- **Complex Setup:** The initial setup is significantly more complex and time-consuming than using a managed service like Cloud Run.
- **Security Responsibility:** You are solely responsible for securing the server, which can be a significant challenge.

---

## Recommendation

- **For a learning-focused project or a hobby project with a strict zero-dollar budget:** The **Oracle ARM Compute** instance is the clear winner. It provides more than enough resources to run the entire stack for free and offers an invaluable learning experience in DevOps and system administration.

- **For a project intended for production use or where reliability is paramount:** **Google Cloud Run** (or a similar managed container service like AWS App Runner/Fargate) is the better choice. While it will have associated costs, the benefits of managed infrastructure, high availability, and scalability are crucial for a production environment.

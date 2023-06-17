

# Deploying Google Kubernetes Engine

1 hour5 Credits



Rate Lab

## Overview

In this lab, you use the Google Cloud Console to build GKE clusters and deploy a sample Pod.

## Objectives

In this lab, you learn how to perform the following tasks:

- Use the Google Cloud Console to build and manipulate GKE clusters
- Use the Google Cloud Console to deploy a Pod
- Use the Google Cloud Console to examine the cluster and Pods

## Lab setup

For each lab, you get a new Google Cloud project and set of resources for a fixed time at no cost.

1. Sign in to Qwiklabs using an **incognito window**.
2. Note the lab's access time (for example, `1:15:00`), and make sure you can finish within that time.
   There is no pause feature. You can restart if needed, but you have to start at the beginning.
3. When ready, click **Start lab**.
4. Note your lab credentials (**Username** and **Password**). You will use them to sign in to the Google Cloud Console.
5. Click **Open Google Console**.
6. Click **Use another account** and copy/paste credentials for **this** lab into the prompts.
   If you use other credentials, you'll receive errors or **incur charges**.
7. Accept the terms and skip the recovery resource page.

**Note:** Do not click **End Lab** unless you have finished the lab or want to restart it. This clears your work and removes the project.

After you complete the initial sign-in steps, the project dashboard opens.

## Task 1. Deploy GKE clusters

In this task, you use the Google Cloud Console and Cloud Shell to deploy GKE clusters.

### Use the Google Cloud Console to deploy a GKE cluster

1. In the Google Cloud Console, on the **Navigation menu** (![Navigation menu icon](https://cdn.qwiklabs.com/tkgw1TDgj4Q%2BYKQUW4jUFd0O5OEKlUMBRYbhlCrF0WY%3D)), click **Kubernetes Engine** > **Clusters**.
2. Click **Create** to begin creating a GKE cluster. Click **configure** for **Standard: You manage your cluster**.
3. Examine the console UI and the controls to change the cluster name, the cluster location, Kubernetes version, the number of nodes, and the node resources such as the machine type in the default node pool.

Clusters can be created across a region or in a single zone. A single zone is the default. When you deploy across a region the nodes are deployed to three separate zones and the total number of nodes deployed will be three times higher.

1. Change the cluster name to **standard-cluster-1** and zone to **us-central1-a**. Leave all the values at their defaults and click **Create**.

The cluster begins provisioning.

**Note:** You need to wait a few minutes for the cluster deployment to complete.

When provisioning is complete, the **Kubernetes Engine > Clusters** page looks like the screenshot:

![Clusters page](https://cdn.qwiklabs.com/syYSoVJHBotCDpSDi4OSQB1KleBOUbhmJXxmXfvrhpI%3D)

Click *Check my progress* to verify the objective.

Deploy GKE cluster

Check my progress



1. Click the cluster name **standard-cluster-1** to view the cluster details
2. You can scroll down the page to view more details.
3. Click the **Storage** and **Nodes** tabs under the cluster name (standard-cluster-1) at the top to view more of the cluster details.

## Task 2. Modify GKE clusters

It is easy to modify many of the parameters of existing clusters using either the Google Cloud Console or Cloud Shell. In this task, you use the Google Cloud Console to modify the size of GKE clusters.

1. In the Google Cloud Console, on the **Navigation menu** (![Navigation menu icon](https://cdn.qwiklabs.com/tkgw1TDgj4Q%2BYKQUW4jUFd0O5OEKlUMBRYbhlCrF0WY%3D)), click **Kubernetes Engine** > **Clusters** > **standard-cluster-1**, click **NODES** at the top of the details page.
2. In **Node Pools** section, click **default-pool**.
3. In the Google Cloud Console, click **RESIZE** at the top of the **Node Pool Details** page.
4. Change the number of nodes from 3 to 4 and click **RESIZE**.

![Resize button on the Node Pool Details page](https://cdn.qwiklabs.com/c%2Bnek4K0%2BlE107Y%2FsEtEcUQ7mp95gW2veEn5SmTGGj8%3D)

1. In the Google Cloud Console, on the **Navigation menu** (![Navigation menu icon](https://cdn.qwiklabs.com/tkgw1TDgj4Q%2BYKQUW4jUFd0O5OEKlUMBRYbhlCrF0WY%3D)), click **Kubernetes Engine** > **Clusters**.

When the operation completes, the **Kubernetes Engine > Clusters** page should show that standard-cluster-1 now has four nodes.

Click *Check my progress* to verify the objective.

Modify GKE clusters

Check my progress



## Task 3. Deploy a sample workload

In this task, using the Google Cloud console you will deploy a Pod running the nginx web server as a sample workload.

1. In the Google Cloud Console, on the **Navigation menu**(![Navigation menu icon](https://cdn.qwiklabs.com/tkgw1TDgj4Q%2BYKQUW4jUFd0O5OEKlUMBRYbhlCrF0WY%3D)), click **Kubernetes Engine** > **Workloads**.
2. Click **Deploy** to show the Create a deployment wizard.
3. Click **Continue** to accept the default container image, nginx:latest, which deploys 3 Pods each with a single container running the latest version of nginx.
4. Scroll to the bottom of the window and click the **Deploy** button leaving the **Configuration** details at the defaults.
5. When the deployment completes your screen will refresh to show the details of your new nginx deployment.

Click *Check my progress* to verify the objective.

Deploy a sample nginx workload

Check my progress



## Task 4. View details about workloads in the Google Cloud Console

In this task, you view details of your GKE workloads directly in the Google Cloud Console.

1. In the Google Cloud Console, on the **Navigation menu** (![Navigation menu icon](https://cdn.qwiklabs.com/tkgw1TDgj4Q%2BYKQUW4jUFd0O5OEKlUMBRYbhlCrF0WY%3D)), click **Kubernetes Engine** > **Workloads**.
2. In the Google Cloud Console, on the **Kubernetes Engine > Workloads** page, click **nginx-1**.

This displays the overview information for the workload showing details like resource utilization charts, links to logs, and details of the Pods associated with this workload.

1. In the Google Cloud Console, click the **Details** tab for the **nginx-1** workload. The Details tab shows more details about the workload including the Pod specification, number and status of Pod replicas and details about the horizontal Pod autoscaler.
2. Click the **Revision History** tab. This displays a list of the revisions that have been made to this workload.
3. Click the **Events** tab. This tab lists events associated with this workload.
4. And then the **YAML** tab. This tab provides the complete YAML file that defines these components and full configuration of this sample workload.
5. Still in the Google Cloud Console's **Details** tab for the **nginx-1** workload, click the **Overview** tab, scroll down to the **Managed Pods** section and click the name of one of the Pods to view the details page for that Pod.
6. The Pod details page provides information on the Pod configuration and resource utilization and the node where the Pod is running.
7. In the **Pod details** page, you can click the Events and Logs tabs to view event details and links to container logs in Cloud Operations.
8. Click the **YAML** tab to view the detailed YAML file for the Pod configuration.


(ns clj-aws.core)
(import 'com.amazonaws.auth.BasicAWSCredentials)
(import '(com.amazonaws.services.ec2 AmazonEC2Client)
        '(com.amazonaws.services.ec2.model DescribeSnapshotsRequest)
        '(com.amazonaws.services.ec2.model DescribeImagesRequest)
        '(com.amazonaws.services.ec2.model DescribeVolumesRequest))

(defn creds 
  []
  (BasicAWSCredentials. (System/getenv "AWS_ACCESS_KEY") (System/getenv "AWS_SECRET_KEY")))

(defn ec2 
  []
  (AmazonEC2Client. (creds)))

(defn snapshots 
  []
  (-> (ec2)
    (.describeSnapshots (DescribeSnapshotsRequest.))
    .getSnapshots))

(defn volumes 
  []
  (-> (ec2)
    (.describeVolumes (DescribeVolumesRequest.))
    .getVolumes))

(defn images 
  []
  (-> (ec2)
    (.describeImages (DescribeImagesRequest.))
    .getImages))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

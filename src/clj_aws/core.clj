(ns clj-aws.core)
(import 'com.amazonaws.auth.BasicAWSCredentials)
(import '(com.amazonaws.services.ec2 AmazonEC2Client)
        '(com.amazonaws.services.ec2.model DescribeSnapshotsRequest)
        '(com.amazonaws.services.ec2.model DeleteSnapshotRequest)
        '(com.amazonaws.services.ec2.model DescribeImagesRequest)
        '(com.amazonaws.services.ec2.model DescribeVolumesRequest))

(def owner "758139277749")

(defn creds 
  []
  (BasicAWSCredentials. (System/getenv "AWS_ACCESS_KEY") (System/getenv "AWS_SECRET_KEY")))

(defn ec2 
  []
  (AmazonEC2Client. (creds)))

(defn snapshots 
  []
  (-> (ec2)
    (.describeSnapshots (doto (DescribeSnapshotsRequest.) (.withOwnerIds [owner])))
    .getSnapshots))

(defn volumes 
  []
  (-> (ec2)
    (.describeVolumes (DescribeVolumesRequest.))
    .getVolumes))

(defn images 
  []
  (let [i (-> (ec2)
    (.describeImages (doto (DescribeImagesRequest.) (.setOwners [owner])))
    .getImages)]
    (apply sorted-set (map #(:imageId (bean %)) i))))

(defn unused-snapshots
  []
  (let [ami (images)
        s (snapshots)]
     (filter #(->> % 
                bean 
                :description 
                (re-find #"(ami-[\w]+)") 
                second 
                ami 
                not) s))) 

(defn delete-snapshot
  [id]
  (-> (ec2)
    (.deleteSnapshot (doto (DeleteSnapshotRequest.) (.setSnapshotId id)))))

(defn delete-unused-snapshots
  []
  (map #(delete-snapshot (.getSnapshotId %)) (unused-snapshots)))

(defn -main
  [& args]
  ())

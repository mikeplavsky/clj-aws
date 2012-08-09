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
    (.describeSnapshots (doto (DescribeSnapshotsRequest.) (.withOwnerIds ["758139277749"])))
    .getSnapshots))

(defn volumes 
  []
  (-> (ec2)
    (.describeVolumes (DescribeVolumesRequest.))
    .getVolumes))

(defn images 
  []
  (let [i (-> (ec2)
    (.describeImages (doto (DescribeImagesRequest.) (.setOwners ["758139277749"])))
    .getImages)]
    (apply sorted-set (map #(:imageId (bean %)) i))))

(defn snapshots-amis
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

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

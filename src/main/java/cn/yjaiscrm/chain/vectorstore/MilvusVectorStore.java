package cn.iyque.chain.vectorstore;

import cn.hutool.core.collection.CollectionUtil;
import cn.iyque.config.IYqueParamConfig;
import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.DataType;
import io.milvus.grpc.DescribeIndexResponse;
import io.milvus.grpc.MutationResult;
import io.milvus.grpc.SearchResults;
import io.milvus.param.*;
import io.milvus.param.collection.*;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.param.index.DescribeIndexParam;
import io.milvus.param.partition.CreatePartitionParam;
import io.milvus.response.QueryResultsWrapper;
import io.milvus.response.SearchResultsWrapper;
import io.milvus.v2.service.vector.request.SearchReq;
import io.milvus.v2.service.vector.request.data.FloatVec;
import io.milvus.v2.service.vector.response.SearchResp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class MilvusVectorStore implements IYqueVectorStore {


    private MilvusServiceClient milvusServiceClient;


    @Autowired
    private IYqueParamConfig paramConfig;



    public MilvusServiceClient getMilvusClient() {
        if (milvusServiceClient == null) {
            synchronized (this) {
                if (milvusServiceClient == null) {
                    try {
                       if(StringUtils.isNotEmpty(paramConfig.getVector().getHost())
                         &&StringUtils.isNotEmpty(paramConfig.getVector().getPort())){
                                     milvusServiceClient = new MilvusServiceClient(
                                             ConnectParam.newBuilder()
                                                     .withHost(paramConfig.getVector().getHost())
                                                     .withPort(Integer.parseInt(paramConfig.getVector().getPort()))
                                                     .withDatabaseName("default")
                                                     .build()
                                     );
                                 }

                    } catch (Exception e) {
                        log.error("Milvus客户端初始化失败", e);
                        throw new RuntimeException("Milvus客户端初始化失败");
                    }
                }
            }
        }
        return milvusServiceClient;
    }

//    @PostConstruct
//    public void init() {
//         if(StringUtils.isNotEmpty(paramConfig.getVector().getHost())
//         &&StringUtils.isNotEmpty(paramConfig.getVector().getPort())){
//             milvusServiceClient = new MilvusServiceClient(
//                     ConnectParam.newBuilder()
//                             .withHost(paramConfig.getVector().getHost())
//                             .withPort(Integer.parseInt(paramConfig.getVector().getPort()))
//                             .withDatabaseName("default")
//                             .build()
//             );
//         }else{
//             log.error("milvus相关配置不可为空");
//         }
//
//    }

    private void createSchema(String kid) {
        FieldType primaryField = FieldType.newBuilder()
                .withName("row_id")
                .withDataType(DataType.Int64)
                .withPrimaryKey(true)
                .withAutoID(true)
                .build();
//        FieldType contentField = FieldType.newBuilder()
//                .withName("content")
//                .withDataType(DataType.VarChar)
//                .withMaxLength(1000)
//                .build();
        FieldType kidField = FieldType.newBuilder()
                .withName("kid")
                .withDataType(DataType.VarChar)
                .withMaxLength(20)
                .build();
        FieldType docIdField = FieldType.newBuilder()
                .withName("docId")
                .withDataType(DataType.VarChar)
                .withMaxLength(20)
                .build();
        FieldType fidField = FieldType.newBuilder()
                .withName("fid")
                .withDataType(DataType.VarChar)
                .withMaxLength(20)
                .build();
        FieldType vectorField = FieldType.newBuilder()
                .withName("fv")
                .withDataType(DataType.FloatVector)
                .withDimension(paramConfig.getVector().getDimension())
                .build();
        CreateCollectionParam createCollectionReq = CreateCollectionParam.newBuilder()
                .withCollectionName(paramConfig.getVector().getCollectionName() + kid)
                .withDescription("local knowledge")
                .addFieldType(primaryField)
//                .addFieldType(contentField)
                .addFieldType(kidField)
                .addFieldType(docIdField)
                .addFieldType(fidField)
                .addFieldType(vectorField)
                .build();
        getMilvusClient().createCollection(createCollectionReq);

        // 创建向量的索引
        IndexType INDEX_TYPE = IndexType.IVF_FLAT;
        String INDEX_PARAM = "{\"nlist\":1024}";
        getMilvusClient().createIndex(
                CreateIndexParam.newBuilder()
                        .withCollectionName(paramConfig.getVector().getCollectionName() + kid)
                        .withFieldName("fv")
                        .withIndexType(INDEX_TYPE)
                        .withMetricType(MetricType.COSINE)
                        .withExtraParam(INDEX_PARAM)
                        .withSyncMode(Boolean.FALSE)
                        .build()
        );

    }

    @Override
    public void newSchema(String kid) {
        createSchema(kid);
    }




    @Override
    public void storeEmbeddings(List<String> chunkList, List<List<Float>> vectorList, String kid, String docId, List<String> fidList) {


        String fullCollectionName = paramConfig.getVector().getCollectionName() + kid;

        // 检查集合是否存在
        HasCollectionParam hasCollectionParam = HasCollectionParam.newBuilder()
                .withCollectionName(fullCollectionName)
                .build();
        R<Boolean> booleanR = getMilvusClient().hasCollection(hasCollectionParam);

        if (booleanR.getStatus() == R.Status.Success.getCode()) {
            boolean collectionExists = booleanR.getData().booleanValue();
            if (!collectionExists) {
                // 集合不存在，创建集合
                List<FieldType> fieldTypes = new ArrayList<>();
                // 假设这里定义 id 字段，根据实际情况修改
                FieldType idField = FieldType.newBuilder()
                        .withName("id")
                        .withDataType(DataType.Int64)
                        .withPrimaryKey(true)
                        .withAutoID(true)
                        .build();
                fieldTypes.add(idField);

                // 定义向量字段
                FieldType vectorField = FieldType.newBuilder()
                        .withName("fv")
                        .withDataType(DataType.FloatVector)
                        .withDimension(vectorList.get(0).size())
                        .build();
                fieldTypes.add(vectorField);

                // 定义其他字段
//                FieldType contentField = FieldType.newBuilder()
//                        .withName("content")
//                        .withDataType(DataType.VarChar)
//                        .withMaxLength(chunkList.size() * 1024) // 根据实际情况修改
//                        .build();
//                fieldTypes.add(contentField);







                FieldType kidField = FieldType.newBuilder()
                        .withName("kid")
                        .withDataType(DataType.VarChar)
                        .withMaxLength(256) // 根据实际情况修改
                        .build();
                fieldTypes.add(kidField);

                FieldType docIdField = FieldType.newBuilder()
                        .withName("docId")
                        .withDataType(DataType.VarChar)
                        .withMaxLength(256) // 根据实际情况修改
                        .build();
                fieldTypes.add(docIdField);

                FieldType fidField = FieldType.newBuilder()
                        .withName("fid")
                        .withDataType(DataType.VarChar)
                        .withMaxLength(256) // 根据实际情况修改
                        .build();
                fieldTypes.add(fidField);

                CreateCollectionParam createCollectionParam = CreateCollectionParam.newBuilder()
                        .withCollectionName(fullCollectionName)
                        .withFieldTypes(fieldTypes)
                        .build();

                R<RpcStatus> collection = getMilvusClient().createCollection(createCollectionParam);
                if (collection.getStatus() == R.Status.Success.getCode()) {
                   log.info("集合 " + fullCollectionName + " 创建成功");

                    // 创建索引
                    CreateIndexParam createIndexParam = CreateIndexParam.newBuilder()
                            .withCollectionName(fullCollectionName)
                            .withFieldName("fv") // 向量字段名
                            .withIndexType(IndexType.IVF_FLAT) // 索引类型
                            .withMetricType(MetricType.COSINE)
                            .withExtraParam("{\"nlist\":1024}") // 索引参数
                            .build();
                    R<RpcStatus> indexResponse = getMilvusClient().createIndex(createIndexParam);
                    if (indexResponse.getStatus() == R.Status.Success.getCode()) {
                       log.info("索引创建成功");
                    } else {
                        log.error("索引创建失败: " + indexResponse.getMessage());
                        return;
                    }
                } else {
                    log.error("集合创建失败: " + collection.getMessage());
                    return;
                }
            }
        } else {
           log.error("检查集合是否存在时出错: " + booleanR.getMessage());
            return;
        }

        if (StringUtils.isNotBlank(docId)) {
            getMilvusClient().createPartition(
                    CreatePartitionParam.newBuilder()
                            .withCollectionName(fullCollectionName)
                            .withPartitionName(docId)
                            .build()
            );
        }

        List<List<Float>> vectorFloatList = new ArrayList<>();
        List<String> kidList = new ArrayList<>();
        List<String> docIdList = new ArrayList<>();
        for (int i = 0; i < Math.min(chunkList.size(), vectorList.size()); i++) {
            List<Float> vector = vectorList.get(i);
            List<Float> vfList = new ArrayList<>();
            for (int j = 0; j < vector.size(); j++) {
                Float value = vector.get(j);
                vfList.add(value.floatValue());
            }
            vectorFloatList.add(vfList);
            kidList.add(kid);
            docIdList.add(docId);
        }
        List<InsertParam.Field> fields = new ArrayList<>();
//        fields.add(new InsertParam.Field("content", chunkList));
        fields.add(new InsertParam.Field("kid", kidList));
        fields.add(new InsertParam.Field("docId", docIdList));
        fields.add(new InsertParam.Field("fid", fidList));
        fields.add(new InsertParam.Field("fv", vectorFloatList));

        InsertParam insertParam = InsertParam.newBuilder()
                .withCollectionName(fullCollectionName)
                .withPartitionName(docId)
                .withFields(fields)
                .build();   System.out.println("=========================");

        R<MutationResult> insert = getMilvusClient().insert(insertParam);
        if (insert.getStatus() == R.Status.Success.getCode()) {
           log.info("插入成功，插入的行数: " + insert.getData().getInsertCnt());
        } else {
            log.error("插入失败: " + insert.getMessage());
        }

        // milvus在将数据装载到内存后才能进行向量计算.
        LoadCollectionParam loadCollectionParam = LoadCollectionParam.newBuilder()
                .withCollectionName(fullCollectionName)
                .build();
        R<RpcStatus> loadResponse = getMilvusClient().loadCollection(loadCollectionParam);
        if (loadResponse.getStatus() != R.Status.Success.getCode()) {
            log.error("加载集合 " + fullCollectionName + " 到内存时出错：" + loadResponse.getMessage());
        }
    }






    @Override
    public void removeByKid(String kid) {
        getMilvusClient().dropCollection(
                DropCollectionParam.newBuilder()
                        .withCollectionName(paramConfig.getVector().getCollectionName() + kid)
                        .build()
        );
    }


    @Override
    public List<String> nearest(List<Float> queryVector, String kid) {
        String fullCollectionName = paramConfig.getVector().getCollectionName() + kid;

        HasCollectionParam hasCollectionParam = HasCollectionParam.newBuilder()
                .withCollectionName(fullCollectionName)
                .build();

        R<Boolean> booleanR = getMilvusClient().hasCollection(hasCollectionParam);
        if (booleanR.getStatus() != R.Status.Success.getCode() || !booleanR.getData().booleanValue()) {
           log.error("集合 " + fullCollectionName + " 不存在或检查集合存在性时出错。");
            return new ArrayList<>();
        }




        DescribeIndexParam describeIndexParam = DescribeIndexParam.newBuilder().withCollectionName(fullCollectionName).build();

        R<DescribeIndexResponse> describeIndexResponseR = getMilvusClient().describeIndex(describeIndexParam);

        if (describeIndexResponseR.getStatus() == R.Status.Success.getCode()) {
           log.info("索引信息: " + describeIndexResponseR.getData().getIndexDescriptionsCount());
        } else {
           log.error("获取索引失败: " + describeIndexResponseR.getMessage());
        }


        List<String> search_output_fields = Arrays.asList("fid", "fv");
        List<Float> fv = new ArrayList<>();
        for (int i = 0; i < queryVector.size(); i++) {
            fv.add(queryVector.get(i).floatValue());
        }
        List<List<Float>> vectors = new ArrayList<>();
        vectors.add(fv);
        String search_param = "{\"nprobe\":10, \"offset\":0}";
        SearchParam searchParam = SearchParam.newBuilder()
                .withCollectionName(paramConfig.getVector().getCollectionName() + kid)
                .withMetricType(MetricType.COSINE)
                .withOutFields(search_output_fields)
                .withTopK(3)
                .withFloatVectors(vectors)
                .withVectorFieldName("fv")
                .withParams(search_param)
                .build();
        System.out.println("SearchParam: " + searchParam.toString());
        R<SearchResults> respSearch = getMilvusClient().search(searchParam);
        if (respSearch.getStatus() == R.Status.Success.getCode()) {
            SearchResults searchResults = respSearch.getData();
            if (searchResults != null) {
                System.out.println(searchResults.getResults());
                SearchResultsWrapper wrapperSearch = new SearchResultsWrapper(searchResults.getResults());
                List<QueryResultsWrapper.RowRecord> rowRecords = wrapperSearch.getRowRecords();
                List<String> resultList = new ArrayList<>();

                if(CollectionUtil.isNotEmpty(rowRecords)){
                    //余弦相似度相似度,过滤掉指定相似度以下的数据
                    List<QueryResultsWrapper.RowRecord> recordList = rowRecords.stream().filter(item ->
                            (float) item.getFieldValues().get("score") >=paramConfig.getVector().getScore()).collect(Collectors.toList());

                    if(CollectionUtil.isNotEmpty(recordList)){
                        for (QueryResultsWrapper.RowRecord rowRecord : recordList) {
                            String content = rowRecord.get("fid").toString();
                            resultList.add(content);
                        }
                    }

                }

                return resultList;
            } else {
                log.info("搜索结果为空");
            }
        } else {
            log.error("搜索操作失败: " + respSearch.getMessage());
        }
        return new ArrayList<>();

    }





}
